#include <graphlab.hpp>
#include <limits>

#include "algorithms.hpp"
#include "utils.hpp"


using namespace std;

static double global_damping_factor;
static double global_dangling_total;
static bool global_directed;

typedef double vertex_data_type;
typedef vertex_data_type gather_type;
typedef graphlab::distributed_graph<vertex_data_type, graphlab::empty> graph_type;


void init_vertex(graph_type::vertex_type &vertex, size_t num_vertices) {
    vertex.data() = 1.0 / num_vertices;
}


class pagerank :
    public graphlab::ivertex_program<graph_type, gather_type>,
    public graphlab::IS_POD_TYPE {

    public:
        edge_dir_type gather_edges(icontext_type& context, const vertex_type& vertex) const {
            return global_directed ? graphlab::IN_EDGES : graphlab::ALL_EDGES;
        }

        gather_type gather(icontext_type& context, const vertex_type& vertex, edge_type& edge) const {
            const vertex_type& other = edge.source().id() == vertex.id() ? edge.target() : edge.source();

            double v = other.data();
            int d = other.num_out_edges() + (global_directed ? 0 : other.num_in_edges());

            return gather_type(v / d);
        }

        void apply(icontext_type& context, vertex_type& vertex, const gather_type &total) {
            size_t num_vertices = context.num_vertices();

            vertex.data() = (1.0 - global_damping_factor) / num_vertices
                          + global_damping_factor * (total + global_dangling_total / num_vertices);

            context.signal(vertex);
        }

        edge_dir_type scatter_edges(icontext_type& context, const vertex_type& vertex) const {
            return graphlab::NO_EDGES;
        }
};

template <typename engine_type>
vertex_data_type get_vertex_data(typename engine_type::icontext_type& context, const graph_type::vertex_type& vertex) {
    int out_degree = vertex.num_out_edges() + (global_directed ? 0 : vertex.num_in_edges());

    if (out_degree == 0) {
        return vertex.data();
    } else {
        return 0.0;
    }
}

template <typename engine_type>
void set_total_residual(typename engine_type::icontext_type& context, vertex_data_type total) {
    global_dangling_total = total;
}


void run_pr(context_t &ctx, bool directed, double damping_factor, int max_iter) {
    typedef graphlab::omni_engine<pagerank> engine_type;
    timer_start();

    // process parameters
    global_directed = directed;
    global_damping_factor = damping_factor;
    ctx.clopts.engine_args.set_option("max_iterations", max_iter);

    // load graph
    timer_next("load graph");
    graph_type graph(ctx.dc);
    load_graph(graph, ctx);
    graph.finalize();
    graph.transform_vertices(boost::bind(init_vertex, _1, graph.num_vertices()));

    // load engine
    timer_next("initialize engine");
    engine_type engine(ctx.dc, graph, "synchronous", ctx.clopts);
    engine.signal_all();

    // After each iteration, we need to collect the sum of vertices which are dangling (i.e., no outgoing edges)
    engine.add_vertex_aggregator<vertex_data_type>("residual",
                                                   &get_vertex_data<engine_type>,
                                                   &set_total_residual<engine_type>);

    engine.aggregate_now("residual");
    engine.aggregate_periodic("residual", 0);

    // run algorithm
    timer_next("run algorithm");
    engine.start();

    // print output
    if (ctx.output_stream) {
        timer_next("print output");
         vector<pair<graphlab::vertex_id_type, vertex_data_type> > data;
         collect_vertex_data(graph, data);

         for (size_t i = 0; i < data.size(); i++) {
             (*ctx.output_stream) << data[i].first << " " << data[i].second << endl;
         }
    }

    timer_end();
}
