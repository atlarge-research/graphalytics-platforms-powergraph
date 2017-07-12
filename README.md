# Graphalytics PowerGraph platform extension

[![Build Status](https://jenkins.tribler.org/buildStatus/icon?job=Graphalytics/Platforms/PowerGraph_master)](https://jenkins.tribler.org/job/Graphalytics/job/Platforms/job/PowerGraph_master/)

## Getting started

Please refer to the documentation of the Graphalytics core (`graphalytics` repository) for an introduction to using Graphalytics.

The following dependencies are required for this platform extension (in parentheses are the recommended versions):

* Any C compiler (`gcc` 5.2.1)
* [PowerGraph](https://github.com/dato-code/PowerGraph) (2.2)
* CMake (3.2.2)
* GNU Make (4.0)
* OpenMPI or MPICH2 (if using PowerGraph distributed)

Download [PowerGraph](https://github.com/dato-code/PowerGraph), unpack into any directory and compile/build using the instructions given by the authors. Note that Graphalytics does not support HDFS as data source for PowerGraph, so it is recommended to compile with the `--no-jvm` flag.

Finally, refer to the documation of the Graphayltics core on how to build and run this platform repository.


## PowerGraph-implementation-specific configuration

Edit `config/powergraph.properties` to change the following settings:

 - `powergraph.home`: Set to the root directory where PowerGraph has been installed.
 - `powergraph.disable_mpi`: Set this flag if PowerGraph has been compiled without MPI support (i.e., configured with `-no_mpi`)
 - `powergraph.num-threads`: Set the number of threads PowerGraph should use.
 - `powergraph.command`: Set the command to run when launching PowerGraph. The default value is "%s %s" where the first argument refers to the binary name and the second arguments refers to the binary arguments. For example, change the value to "mpirun -np 2 %s %s" to execute PowerGraph using MPI on two nodes.


## Known Issues

* PowerGraph does not support machines with more than 64 threads. A workaround has been proposed in [this issue](https://github.com/tudelft-atlarge/graphalytics-platforms-powergraph/issues/4).

