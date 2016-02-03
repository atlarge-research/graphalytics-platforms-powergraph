#include <graphlab.hpp>
#include <fstream>



using namespace std;

typedef graphlab::distributed_graph<graphlab::empty, graphlab::empty> graph_type;

bool parse_vertex_line(graph_type &graph, const std::string &file, const std::string &line) {
    if (line.empty() || line[0] == '#') {
        return true;
    }

    char *dst;
    size_t id = strtoul(line.c_str(), &dst, 10);
    if (dst == line.c_str()) return false;

    graph.add_vertex(id);
    return true;
}

bool parse_edge_line(graph_type &graph, const std::string &file, const std::string &line) {
    if (line.empty() || line[0] == '#') {
        return true;
    }

    char *dst;
    size_t source = strtoul(line.c_str(), &dst, 10);
    if (dst == line.c_str()) return false;

    char *end;
    size_t target = strtoul(dst, &end, 10);
    if (dst == end) return false;

    if (source != target) graph.add_edge(source, target);
    return true;
}

int main(int argc, char **argv) {
    graphlab::mpi_tools::init(argc, argv);
    graphlab::distributed_control dc;
    global_logger().set_log_level(LOG_INFO);

    graphlab::command_line_options clopts("Breadth-first search algorithm");

    // General options
    string vertex_file;
    clopts.attach_option("vertices-file", vertex_file,
            "Path to vertices file of the graph");
    clopts.add_positional("vertices-file");

    string edge_file;
    clopts.attach_option("edges-file", edge_file,
            "Path to edges file of the the graph");
    clopts.add_positional("edges-file");

    string output_file;
    clopts.attach_option("output-prefix", output_file,
            "Write output to files with given prefix");
    clopts.add_positional("output-prefix");

    int num_files = 16;
    clopts.attach_option("num-files", num_files,
            "Number of output files to write");

    if (!clopts.parse(argc, argv)) {
        dc.cerr() << "Error in parsing command line arguments." << endl;
        return EXIT_FAILURE;
    }

    if (vertex_file.empty() || edge_file.empty() || output_file.empty()) {
        dc.cerr() << "Graph not specified. Cannot continue" << endl;
        return EXIT_FAILURE;
    }

    graphlab::distributed_graph<graphlab::empty, graphlab::empty> graph(dc);
    graph.load(vertex_file, parse_vertex_line);
    graph.load(edge_file, parse_edge_line);
    graph.finalize();

    graph.save_format(output_file, "graphjrl", false, num_files);

    graphlab::mpi_tools::finalize();
    return EXIT_SUCCESS;
}
