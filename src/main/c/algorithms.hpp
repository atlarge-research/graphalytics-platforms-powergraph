#ifndef ALGORITHMS_H
#define ALGORITHMS_H

#include <graphlab.hpp>
#include <ostream>
#include <string>

struct context_t {
    std::string vertex_file;
    std::string edge_file;
    graphlab::distributed_control& dc;
    graphlab::command_line_options& clopts;
    bool output_enabled;
    std::ostream *output_stream;
};

namespace graphalytics {

    namespace bfs {
        void run(
                context_t &ctx,
                bool directed,
                graphlab::vertex_id_type source);
    }

    namespace pr {
        void run(
                context_t &ctx,
                bool directed,
                double damping_factor,
                int max_iter);
    }

    namespace cd {
        void run(
                context_t &ctx,
                int max_iter);
    }

    namespace conn {
        void run(
                context_t &ctx);
    }

    namespace lcc {
        void run(
                context_t &ctx,
                bool directed);
    }

    namespace sssp {
        void run(
                context_t &ctx,
                bool directed,
                graphlab::vertex_id_type source);
    }
}

#endif
