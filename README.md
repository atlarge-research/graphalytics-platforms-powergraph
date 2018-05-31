# Graphalytics PowerGraph platform extension

[![Build Status](https://jenkins.tribler.org/buildStatus/icon?job=Graphalytics/Platforms/PowerGraph_master)](https://jenkins.tribler.org/job/Graphalytics/job/Platforms/job/PowerGraph_master/)

### Getting started

This is a [Graphalytics](https://github.com/ldbc/ldbc_graphalytics/) benchmark driver for the PowerGraph. Please refer to the documentation of [Graphalytics core](https://github.com/ldbc/ldbc_graphalytics) for an introduction to using Graphalytics.

  - Make sure that you have [installed Graphalytics](https://github.com/ldbc/ldbc_graphalytics/wiki/Documentation%3A-Software-Build#the-core-repository). 
  - Download the source code from this repository.
  - Execute `mvn clean package` in the root directory (See details in [Software Build](https://github.com/ldbc/ldbc_graphalytics/wiki/Documentation:-Software-Build)).
  - Extract the distribution from  `graphalytics-{graphalytics-version}-powergraph-{platform-version}.tar.gz`.

The following dependencies are required for this platform extension:

| Software           | Version (tested) | Usage      | Description               | Provided |
|-------------------|------------------|------------|---------------------------|----------|
| C Compiler        | gcc 5.2.1        | Build      | Building PowerGraph code  | -        |
| PowerGraph        | 2.2              | Platform   | PowerGraph implementation | -        |
| CMake             | 3.2.2            | Build      | Building PowerGraph code  | -        |
| GNU Make          | 4.0              | Build      | Building PowerGraph code  | -        |
| OpenMPI or MPICH2 | 1.10.3           | Deployment | Job deployment            | -        |

Download [PowerGraph](https://github.com/jegonzal/PowerGraph), unpack into any directory, patch the missing `CMakeLists.txt` file using a diff from bin/utils/ and fully compile/build using the instructions given by the authors. Note that Graphalytics does not support HDFS as data source for PowerGraph, so it is recommended to compile with the `--no_jvm` flag.

Alternatively, one may use the `build-distribution.sh` script that performs the steps described above in an automated fashion.

Finally, refer to the documentation of the Graphalytics core on how to build and run this platform repository.


### PowerGraph-implementation-specific configuration

Edit `config/powergraph.properties` to change the following settings:

 - `platform.powergraph.home`: Set to the root directory where PowerGraph has been installed.
 - `platform.powergraph.num-threads`: Set the number of threads PowerGraph should use.
 - `platform.powergraph.nodes`: Set the the names of computation nodes, with format e.g., `10.149.0.55\,10.149.0.56` (note: IP's separated between 
 `\,` non-separated by spaces).


### Known Issues

* PowerGraph does not support machines with more than 64 threads. A workaround has been proposed in [this issue](https://github.com/tudelft-atlarge/graphalytics-platforms-powergraph/issues/4).
* The PowerGraph installation process is somewhat outdated, it has a few broken links to dependencies. Patching the `CMakeLists.txt` file with our [diff](https://github.com/atlarge-research/graphalytics-platforms-powergraph/tree/master/bin/utils/CMakeLists_a038f97.diff) fixes these broken URIs.

### Running the benchmark

To execute a Graphalytics benchmark on PowerGraph (using this driver), follow the steps in the Graphalytics tutorial on [Running Benchmark](https://github.com/ldbc/ldbc_graphalytics/wiki/Manual%3A-Running-Benchmark).
