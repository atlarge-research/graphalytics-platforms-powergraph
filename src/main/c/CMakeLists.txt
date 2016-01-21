project (PowerGraphApps CXX)
cmake_minimum_required (VERSION 2.6)

include (config.cmake)

if (NOT POWERGRAPH_HOME)
    get_filename_component(CONFIG_PATH config.cmake ABSOLUTE)
    message(FATAL_ERROR "Before running cmake, please specify $POWERGRAPH_HOME in: ${CONFIG_PATH}")
endif ()

set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread -fopenmp -Wno-unused-variable -Wno-unused-parameter")
set (CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_DEBUG} -Wall -pedantic -Wextra")
set (CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_RELEASE} -O3")
set (CMAKE_EXPORT_COMPILE_COMMANDS 1)

include_directories (SYSTEM ${POWERGRAPH_HOME}/deps/local/include)
include_directories (SYSTEM ${POWERGRAPH_HOME}/src)

link_directories (${POWERGRAPH_HOME}/deps/local/lib)
link_directories (${POWERGRAPH_HOME}/release/src/graphlab)

set (LIBS ${LIBS} graphlab)
set (LIBS ${LIBS} zookeeper_mt zookeeper_st)
set (LIBS ${LIBS} boost_context boost_filesystem boost_program_options boost_system boost_iostreams boost_date_time)
set (LIBS ${LIBS} tcmalloc event event_pthreads hdfs z)

if (${USE_MPI})
    set (LIBS ${LIBS} mpi mpi++)
endif ()

if (${USE_JVM})
    set (LIBS ${LIBS} jvm)
endif ()

add_executable (main main.cpp bfs.cpp cd.cpp conn.cpp pr.cpp lcc.cpp)
target_link_libraries (main ${LIBS})
