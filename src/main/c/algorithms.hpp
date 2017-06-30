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
                graphlab::vertex_id_type source,
                std::string job_id);
    }

    namespace pr {
        void run(
                context_t &ctx,
                bool directed,
                double damping_factor,
                int max_iter,
                std::string job_id);
    }

    namespace cdlp {
        void run(
                context_t &ctx,
                int max_iter,
                std::string job_id);
    }

    namespace wcc {
        void run(
                context_t &ctx,
                std::string job_id);
    }

    namespace lcc {
        void run(
                context_t &ctx,
                bool directed,
                std::string job_id);
    }

    namespace sssp {
        void run(
                context_t &ctx,
                bool directed,
                graphlab::vertex_id_type source,
                std::string job_id);
    }
}

#endif
