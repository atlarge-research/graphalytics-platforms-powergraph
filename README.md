# Graphalytics PowerGraph platform extension

## Getting started

Please refer to the documentation of the Graphalytics core (`graphalytics` repository) for an introduction to using Graphalytics.

The following dependencies are required for this platform extension (in parentheses are the recommended versions):

* Any C compiler (`gcc` 5.2.1)
* [PowerGraph](https://github.com/dato-code/PowerGraph) (2.2)
* CMake (3.2.2)
* GNU Make (4.0)
* OpenMPI or MPICH2 (if using PowerGraph distributed)

Download [PowerGraph](https://github.com/dato-code/PowerGraph), unpack into any directory and compile/build using the instructions given by the authors. 

Change the parameters in `src/main/c/config.mk` to the following values:

* `POWERGRAPH_HOME`: root directory where PowerGraph is installed.
* `NO_JVM`: if PowerGraph was compiled without JVM support (-no_jvm)
* `NO_MPI`: If PowerGraph was compiled without MPI support (-no_mpi)

Finally, refer to the documation of the Graphayltics core on how to build and run this platform repository.


## PowerGraph-implementation-specific configuration

Edit `config/powergraph.properties` to change the following settings:

 - `powergraph.command`: Set the command to run when launching PowerGraph. The default value is "%s %s" where the first argument refers to the binary name and the second arguments refers to the binary arguments. For example, change the value to "mpirun -np 2 %s %s" to execute PowerGraph using MPI on two nodes.
