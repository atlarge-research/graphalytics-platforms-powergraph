project (PowerGraphApps CXX C)
cmake_minimum_required (VERSION 2.6)

include (config.cmake)

if (NOT POWERGRAPH_HOME)
    get_filename_component(CONFIG_PATH config.cmake ABSOLUTE)
    message(FATAL_ERROR "Before running cmake, please specify $POWERGRAPH_HOME in: ${CONFIG_PATH}")
endif ()

find_package(OpenMP REQUIRED)
set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OpenMP_CXX_FLAGS}")


set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread -fopenmp -Wno-unused-variable -Wno-unused-parameter")
set (CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_DEBUG} -Wall -pedantic -Wextra")
set (CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_RELEASE} -O3")
set (CMAKE_EXPORT_COMPILE_COMMANDS 1)

include_directories (SYSTEM ${POWERGRAPH_HOME}/deps/local/include)
include_directories (SYSTEM ${POWERGRAPH_HOME}/src)

link_directories (${POWERGRAPH_HOME}/deps/local/lib)
link_directories (${POWERGRAPH_HOME}/release/src/graphlab)

set (CMAKE_C_FLAGS "-asdfasdf")
find_package(Threads REQUIRED)
set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${CMAKE_THREAD_LIBS_INIT}")

set (LIBS ${LIBS} graphlab)
set (LIBS ${LIBS} zookeeper_mt zookeeper_st)
set (LIBS ${LIBS} boost_context boost_filesystem boost_program_options boost_system boost_iostreams boost_date_time)
set (LIBS ${LIBS} tcmalloc event event_pthreads z)

if (NOT ${NO_MPI})
    find_package(MPI REQUIRED)

    include_directories(${MPI_CXX_INCLUDE_PATH})
    set (LIBS ${LIBS} ${MPI_CXX_LIBRARIES})
    set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${MPI_CXX_COMPILE_FLAGS}")
    set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${MPI_CXX_LINK_FLAGS}")
endif ()

if (NOT ${NO_JVM})
    set (LIBS "${LIBS} jvm hdfs")
endif ()

add_executable (main main.cpp bfs.cpp cd.cpp conn.cpp pr.cpp lcc.cpp)
target_link_libraries (main ${LIBS})
