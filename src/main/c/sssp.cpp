/*
 * Copyright 2015 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <graphlab.hpp>
#include <stdint.h>

#include "algorithms.hpp"
#include "utils.hpp"

#ifdef GRANULA
#include "granula.hpp"
#endif


namespace graphalytics {
namespace sssp {

using namespace std;

typedef double vertex_data_type;
typedef double edge_data_type;
typedef graphlab::empty gather_type;
typedef min_reducer<vertex_data_type> msg_type;
typedef graphlab::distributed_graph<vertex_data_type, edge_data_type> graph_type;

static void init_vertex(graph_type::vertex_type &vertex) {
    vertex.data() = numeric_limits<vertex_data_type>::max();
}

static bool global_directed;


class single_source_shortest_path :
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
            if (last_msg.get() < vertex.data()) {
                vertex.data() = last_msg.get();
                changed = true;
            } else {
                changed = false;
            }
        }

        edge_dir_type scatter_edges(icontext_type& context, const vertex_type& vertex) const {
            return changed
                    ? (global_directed
                            ? graphlab::OUT_EDGES
                            : graphlab::ALL_EDGES)
                    : graphlab::NO_EDGES;
        }

        void scatter(icontext_type& context, const vertex_type& vertex, edge_type& edge) const {
            const vertex_type& other = edge.target().id() == vertex.id() ? edge.source() : edge.target();
            vertex_data_type new_dist = vertex.data() + edge.data();

            if (other.data() > new_dist) {
                context.signal(other, msg_type(new_dist));
            }
        }
};

bool edge_data_parser(const string &line, double &data) {
    const char *start = line.c_str();
    char *end;
    data = strtod(start, &end);
    return start < end;
}

void run(context_t &ctx, bool directed, graphlab::vertex_id_type source, string job_id) {
    bool is_master = ctx.dc.procid() == 0;
    timer_start(is_master);

#ifdef GRANULA
    granula::startMonitorProcess(getpid());
    granula::operation powergraphJob("PowerGraph", "Id.Unique", "Job", "Id.Unique");
    granula::operation loadGraph("PowerGraph", "Id.Unique", "LoadGraph", "Id.Unique");
    if(is_master) {
        cout<<powergraphJob.getOperationInfo("StartTime", powergraphJob.getEpoch())<<endl;
        cout<<loadGraph.getOperationInfo("StartTime", loadGraph.getEpoch())<<endl;
    }

    granula::linkNode(job_id);
    granula::linkProcess(getpid(), job_id);
#endif

    // process parameters
    global_directed = directed;

    // load graph
    timer_next("load graph");
    graph_type graph(ctx.dc);
    load_graph_properties(graph, ctx, default_parser<vertex_data_type>, edge_data_parser);
    graph.finalize();
    graph.transform_vertices(init_vertex);

#ifdef GRANULA
    if(is_master) {
        cout<<loadGraph.getOperationInfo("EndTime", loadGraph.getEpoch())<<endl;
    }
#endif

    // start engine
    timer_next("initialize engine");
    graphlab::omni_engine<single_source_shortest_path> engine(ctx.dc, graph, "synchronous", ctx.clopts);
    engine.signal(source, msg_type(0.0));

#ifdef GRANULA
    granula::operation processGraph("PowerGraph", "Id.Unique", "ProcessGraph", "Id.Unique");
    if(is_master) {
        cout<<processGraph.getOperationInfo("StartTime", processGraph.getEpoch())<<endl;
    }
#endif

    // run algorithm
    timer_next("run algorithm");
    engine.start();

#ifdef GRANULA
    if(is_master) {
        cout<<processGraph.getOperationInfo("EndTime", processGraph.getEpoch())<<endl;
    }
#endif

#ifdef GRANULA
    granula::operation offloadGraph("PowerGraph", "Id.Unique", "OffloadGraph", "Id.Unique");
    if(is_master) {
        cout<<offloadGraph.getOperationInfo("StartTime", offloadGraph.getEpoch())<<endl;
    }
#endif

    // print output
    if (ctx.output_enabled) {
    	timer_next("print output");
        vector<pair<graphlab::vertex_id_type, vertex_data_type> > data;
        collect_vertex_data(graph, data, is_master);

        for (size_t i = 0; i < data.size(); i++) {
            vertex_data_type d = data[i].second;

            // If the distance is the max value for vertex_data_type
            // then the vertex is not connected to the source vertex.
            // According to specs, the output should be +inf
            if (d == numeric_limits<vertex_data_type>::max()) {
                (*ctx.output_stream) << data[i].first << " Infinity" << endl;
            } else {
                (*ctx.output_stream) << data[i].first << " " << d << endl;
            }

        }
    }

    timer_end();

#ifdef GRANULA
    if(is_master) {
        cout<<offloadGraph.getOperationInfo("EndTime", offloadGraph.getEpoch())<<endl;
        cout<<powergraphJob.getOperationInfo("EndTime", powergraphJob.getEpoch())<<endl;
    }
    granula::stopMonitorProcess(getpid());
#endif

}

}
}
