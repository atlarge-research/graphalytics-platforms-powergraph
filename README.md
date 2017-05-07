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

Finally, refer to the documation of the Graphayltics core on how to build and run this platform repository.


## PowerGraph-implementation-specific configuration

Edit `config/platform.properties` to change the following settings:

 - `platform.powergraph.home`: Set to the root directory where PowerGraph has been installed.
 - `platform.powergraph.disable_mpi`: Set this flag if PowerGraph has been compiled without MPI support (i.e., configured with `-no_mpi`)
 - `platform.powergraph.num-threads`: Set the number of threads PowerGraph should use.
 - `platform.powergraph.command`: Set the command to run when launching PowerGraph. The default value is "%s %s" where the first argument refers to the binary name and the second arguments refers to the binary arguments. For example, change the value to "mpirun -np 2 %s %s" to execute PowerGraph using MPI on two nodes.
