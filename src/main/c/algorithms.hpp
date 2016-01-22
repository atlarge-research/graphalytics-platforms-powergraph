#include <graphlab.hpp>
#include <ostream>
#include <string>

struct context_t {
    std::string vertex_file;
    std::string edge_file;
    graphlab::distributed_control& dc;
    graphlab::command_line_options& clopts;
    std::ostream *output_stream;
};

void run_bfs(
        context_t &ctx,
        bool directed,
        graphlab::vertex_id_type source);

void run_pr(
        context_t &ctx,
        bool directed,
        double damping_factor,
        int max_iter);

void run_cd(
        context_t &ctx,
        int max_iter);

void run_conn(
        context_t &ctx);

void run_lcc(
        context_t &ctx,
        bool directed);
