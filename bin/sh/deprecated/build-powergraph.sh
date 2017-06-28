function buildLibraries {

module unload intel-mpi
module unload gcc
module load openmpi/open64/64/1.10.1 
module load openmpi/gcc/64/1.10.1 
module list

# change Boost src to http://kent.dl.sourceforge.net/project/boost/boost/1.53.0/boost_1_53_0.tar.gz in CMakeList.
cd /var/scratch/wlngai/graphalytics/large-platforms/powergraph/src/
rm -rf deps/*
./configure --no_jvm
cd release
make
}


function buildProgram {

module unload intel-mpi
module unload gcc
module load openmpi/open64/64/1.10.1 
module load openmpi/gcc/64/1.10.1 
module list


POWERGRAPH_HOME=/var/scratch/wlngai/graphalytics/large-platforms/powergraph/src/

mkdir -p bin/standard
(cd bin/standard && cmake -DCMAKE_BUILD_TYPE=Release ../../src/main/c -DPOWERGRAPH_HOME=$POWERGRAPH_HOME -DNO_JVM=1 -DNO_MPI=0 && make all)

mkdir -p bin/granula
(cd bin/granula && cmake -DCMAKE_BUILD_TYPE=Release -DGRANULA=1 ../../src/main/c -DPOWERGRAPH_HOME=$POWERGRAPH_HOME -DNO_JVM=1 -DNO_MPI=0 && make all)

rm -f bin/*/CMakeCache.txt
sed -i '48,61d' sh/prepare-benchmark.sh 

}


buildProgram
