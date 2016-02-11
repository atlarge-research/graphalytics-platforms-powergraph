#include <graphlab.hpp>
#include <limits>

#include "algorithms.hpp"
#include "utils.hpp"

namespace graphalytics {
namespace conn {

using namespace std;

typedef graphlab::vertex_id_type vertex_data_type;
typedef graphlab::empty edge_data_type;
typedef graphlab::empty gather_type;
typedef min_reducer<vertex_data_type> msg_type;
typedef graphlab::distributed_graph<vertex_data_type, edge_data_type> graph_type;

const vertex_data_type INVALID_LABEL = numeric_limits<vertex_data_type>::max();

class weakly_connected_components :
    public graphlab::ivertex_program<graph_type, gather_type, msg_type>,
    public graphlab::IS_POD_TYPE {

    msg_type last_msg;
    bool changed;

    public:
        void init(icontext_type& context, const vertex_type& vertex, const msg_type& msg) {
            last_msg = msg;
        }

        edge_dir_type gather_edges(icontext_type& context, const vertex_type& vertex) const {
            return graphlab::NO_EDGES;
        }

        void apply(icontext_type& context, vertex_type& vertex, const gather_type &total) {
            // First iteration, set label to vertex id
            if (last_msg.get() == INVALID_LABEL) {
                changed = true;
                vertex.data() = vertex.id();
            }

            // Neighbor updated, if label of neighbor is lower -> change label
            else if (last_msg.get() < vertex.data()) {
                vertex.data() = last_msg.get();
                changed = true;
            }

            // Otherwise, do not update
            else {
                changed = false;
            }
        }

        edge_dir_type scatter_edges(icontext_type& context, const vertex_type& vertex) const {
            return changed ? graphlab::ALL_EDGES : graphlab::NO_EDGES;
        }

        void scatter(icontext_type& context, const vertex_type& vertex, edge_type& edge) const {
            const vertex_type& other = vertex.id() == edge.source().id() ? edge.target() : edge.source();

            if (vertex.data() < other.data()) {
                context.signal(other, msg_type(vertex.data()));
            }
        }
};



void run(context_t &ctx) {
    bool is_master = ctx.dc.procid() == 0;
    timer_start(is_master);

    // load graph
    timer_next("load graph");
    graph_type graph(ctx.dc);
    load_graph(graph, ctx);
    graph.finalize();

    // run engine
    timer_next("initialize engine");
    graphlab::omni_engine<weakly_connected_components> engine(ctx.dc, graph, "synchronous", ctx.clopts);
    engine.signal_all(msg_type(INVALID_LABEL));

    // run algorithm
    timer_next("run algorithm");
    engine.start();

    // print output
    if (ctx.output_stream) {
        timer_next("print output");
        vector<pair<graphlab::vertex_id_type, vertex_data_type> > data;
        collect_vertex_data(graph, data, is_master);

        for (size_t i = 0; i < data.size(); i++) {
            (*ctx.output_stream) << data[i].first << " " << data[i].second << endl;
        }
    }

    timer_end();
}

}
}
