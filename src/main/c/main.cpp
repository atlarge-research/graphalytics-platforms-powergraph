#include <graphlab.hpp>
#include <fstream>

#include "algorithms.hpp"


using namespace std;

int main(int argc, char **argv) {
    graphlab::mpi_tools::init(argc, argv);
    graphlab::distributed_control dc;
    global_logger().set_log_level(LOG_INFO);

    graphlab::command_line_options clopts("Breadth-first search algorithm");


    // PageRank specific options
    double pr_damping_factor = 0.85;
    clopts.attach_option("damping-factor", pr_damping_factor,
            "Damping factor to use (PageRank only)");

    // BFS specific options
    graphlab::vertex_id_type traverse_source_vertex = 0;
    clopts.attach_option("source-vertex", traverse_source_vertex,
            "Source vertex ot use (BFS and SSSP only)");

    // General options
    string vertex_file;
    clopts.attach_option("vertices-file", vertex_file,
            "Path to vertices file of the graph");
    clopts.add_positional("vertices-file");

    string edge_file;
    clopts.attach_option("edges-file", edge_file,
            "Path to edges file of the the graph");
    clopts.add_positional("edges-file");

    bool directed = false;
    clopts.attach_option("directed", directed,
            "Whether the graph is directed");
    clopts.add_positional("directed");

    string algorithm = "";
    clopts.attach_option("algorithm", algorithm,
            "Algorithm to use (bfs/pr/conn/cd/lcc)");
    clopts.add_positional("algorithm");

    int max_iter = 10;
    clopts.attach_option("max-iterations", max_iter,
            "Maximum number of iterations to use");

    string output_file;
    clopts.attach_option("output-file", output_file,
            "Write output to given file");

    bool output_console = false;
    clopts.attach_option("output-console", output_console,
            "Write output to stdout");


    if (!clopts.parse(argc, argv)) {
        dc.cerr() << "Error in parsing command line arguments." << endl;
        return EXIT_FAILURE;
    }

    if (vertex_file.empty() || edge_file.empty()) {
        dc.cerr() << "Graph not specified. Cannot continue" << endl;
        return EXIT_FAILURE;
    }

    ostream *output_stream = NULL;
    ofstream *file_stream = NULL;

    if (output_console) {
        output_stream = &dc.cout();
    } else if (!output_file.empty()) {
        file_stream = new ofstream(output_file.c_str(), ofstream::out);

        if (!file_stream->good()) {
            dc.cerr() << "error occured while opening file" << endl;
            return EXIT_FAILURE;
        }

        output_stream = file_stream;
    }

    context_t ctx = {
        .vertex_file = vertex_file,
        .edge_file = edge_file,
        .dc = dc,
        .clopts = clopts,
        .output_stream = output_stream
    };

    if (algorithm == "bfs") {
        run_bfs(ctx, directed, traverse_source_vertex);
    } else if (algorithm == "conn") {
        run_conn(ctx);
    } else if (algorithm == "pr") {
        run_pr(ctx, directed, pr_damping_factor, max_iter);
    } else if (algorithm == "cd") {
        run_cd(ctx, max_iter);
    } else if (algorithm == "lcc") {
        run_lcc(ctx, directed);
    } else if (algorithm == "sssp") {
        run_sssp(ctx, directed, traverse_source_vertex);
    } else {
        dc.cerr() << "Unknown algorithm specified: " << algorithm << endl;
        return EXIT_FAILURE;
    }

    if (file_stream != NULL) {
        bool good = file_stream->good();
        file_stream->flush();
        file_stream->close();

        if (!good) {
            dc.cerr() << "error occured while writing to file while write to file" << endl;
            return EXIT_FAILURE;
        }
    }

    graphlab::mpi_tools::finalize();
    return EXIT_SUCCESS;
}
