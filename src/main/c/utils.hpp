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
#ifndef UTILS_HPP
#define UTILS_HPP

#include <graphlab.hpp>
#include <fstream>
#include <ostream>
#include <sys/time.h>
#include <limits>
#include <string>
#include <vector>



template <typename T>
class histogram {

    // We cannot define INVALID_ITEM as a constant since numeric_limits::max is
    // an expression and cannot be assigned to a constant field. As a workaround,
    // we defined it as a macro and undefine it at the end of the class definition.
#define INVALID_ITEM (std::numeric_limits<T>::max())

    public:
        typedef typename boost::unordered_map<T, size_t> map_type;
        typedef typename map_type::const_iterator map_iterator_type;

        T first_item;
        map_type *data;

        histogram() {
            first_item = INVALID_ITEM;
            data = NULL;
        }

        histogram(T t) {
            first_item = t;
            data = NULL;
        }

        histogram(const histogram<T> &other) {
            first_item = INVALID_ITEM;
            data = NULL;
            *this = other;
        }

        histogram<T>& operator +=(const histogram<T>& other) {
            if (data == NULL) {
                data = new map_type;
                if (first_item != INVALID_ITEM) (*data)[first_item] = 1;
            }

            if (other.data != NULL) {
                for (map_iterator_type it = other.data->begin(); it != other.data->end(); it++) {
                    (*data)[it->first] += it->second;
                }
            } else if (other.first_item != INVALID_ITEM) {
                (*data)[other.first_item] += 1;
            }

            return *this;
        }

        histogram<T>& operator=(const histogram<T>& other) {
            if (data) delete data;
            data = NULL;

            first_item = other.first_item;
            if (other.data != NULL) data = new map_type(*other.data);

            return *this;
        }

        const map_type get() const {
            if (data == NULL) {
                map_type tmp;
                if (first_item != INVALID_ITEM) tmp[first_item] = 1;
                return tmp;
            }

            return *data;
        }

        void save(graphlab::oarchive& oarc) const {
            if (data == NULL) {
                map_type tmp;
                if (first_item != INVALID_ITEM) tmp[first_item] = 1;
                oarc << tmp;
            } else {
                oarc << *data;
            }
        }

        void load(graphlab::iarchive& iarc) {
            if (data) delete data;
            data = new map_type;
            iarc >> *data;
        }

        ~histogram() {
            if (data) delete data;
        }

#undef INVALID_ITEM
};

template <typename T>
struct min_reducer : public graphlab::IS_POD_TYPE {
    T value;

    public:
        min_reducer(T v=std::numeric_limits<T>::max()) {
            value = v;
        }

        min_reducer<T>& operator +=(const min_reducer<T>& other) {
            value = std::min(value, other.value);
            return *this;
        }

        T get() const {
            return value;
        }
};

template <typename A, typename B>
std::pair<B, A> reverse(std::pair<A, B> p) {
    return std::make_pair(p.second, p.first);
}

template <typename G>
void collect_vertex_data(G &graph,
        std::vector<std::pair<typename G::vertex_id_type,
                              typename G::vertex_data_type> > &result,
                              bool originator) {

    for (size_t i = 0, n = graph.num_local_vertices(); i < n; i++) {
        const typename G::local_vertex_type &v = graph.l_vertex(i);

        if (v.owned()) {
            result.push_back(std::make_pair(v.global_id(), v.data()));
        }
    }

    if (originator) {
        std::vector<std::pair<typename G::vertex_id_type,
                              typename G::vertex_data_type> > buffer;

        for (size_t pid = 1; pid < graph.dc().numprocs(); pid++) {
            graph.dc().recv_from(pid, buffer);
            result.insert(result.end(), buffer.begin(), buffer.end());
            buffer.clear();
        }

    } else {
        graph.dc().send_to(0, result);
        result.clear();
    }
}

template <typename D>
bool default_parser(const std::string &p, D &data) {
    return true;
}

static bool safe_strtoull(const char **str, size_t &val) {
    const char *before = *str;
    char *after;

    val = strtoull(before, &after, 10);
    *str = after;

    return before < after;
}

template <typename G, typename F>
bool parse_vertex_line(G &graph, const std::string &file, const std::string &line, const F &parser) {
    typedef typename G::vertex_data_type vertex_data_type;

    const char *str = line.c_str();
    size_t id;
    vertex_data_type data;

    while (isspace(*str)) str++;

    if (*str == '\0' || *str == '#') {
        return true;
    }

    if (!safe_strtoull(&str, id)) {
        return false;
    }

    while (isspace(*str)) str++;

    if (!parser(std::string(str), data)) {
        return false;
    }

    graph.add_vertex(id, data);
    return true;
}

template <typename G, typename F>
bool parse_edge_line(G &graph, const std::string &file, const std::string &line, const F &parser) {
    typedef typename G::edge_data_type edge_data_type;

    const char *str = line.c_str();
    size_t source, target;
    edge_data_type data;

    while (isspace(*str)) str++;

    if (*str == '\0' || *str == '#') {
        return true;
    }

    if (!safe_strtoull(&str, source)) {
        return false;
    }

    while (isspace(*str)) str++;

    if (!safe_strtoull(&str, target)) {
        return false;
    }

    while (isspace(*str)) str++;

    if (!parser(std::string(str), data)) {
        return false;
    }

    if (source == target) {
        return true;
    }

    graph.add_edge(source, target, data);

    return true;
}

template <typename G, typename FV, typename FE>
void load_graph_properties(G &graph, context_t &ctx, const FV &vertex_parser, const FE &edge_parser) {
    graph.load(ctx.vertex_file, boost::bind(parse_vertex_line<G, FV>, _1, _2, _3, boost::ref(vertex_parser)));
    graph.load(ctx.edge_file, boost::bind(parse_edge_line<G, FE>, _1, _2, _3, boost::ref(edge_parser)));
}

template <typename G>
void load_graph(G &graph, context_t &ctx) {
    load_graph_properties(graph, ctx,
            default_parser<typename G::vertex_data_type>,
            default_parser<typename G::edge_data_type>);
}

static double timer() {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec + tv.tv_usec / 1000000.0;
}

static bool timer_enabled;
static std::vector<std::pair<std::string, double> > timers;

static void timer_start(bool enabled=true) {
    timers.clear();
    timer_enabled = enabled;
}

static void timer_next(std::string name) {
    if (timer_enabled) {
        timers.push_back(std::make_pair(name, timer()));
    }
}

static void timer_end() {
    if (timer_enabled) {
        timer_next("end");

        std::cerr << "Timing results:" << std::endl;

        for (size_t i = 0; i < timers.size() - 1; i++) {
            std::string &name = timers[i].first;
            double time = timers[i + 1].second - timers[i].second;

            std::cerr << " - "  << name << ": " << time << " sec" <<  std::endl;
        }

        timers.clear();
    }
}

#endif
