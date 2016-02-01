#!/bin/sh
#
# Copyright 2015 Delft University of Technology
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

export platform="powergraph"

# Build binaries
if [ -z $POWERGRAPH_HOME ]; then
    POWERGRAPH_HOME=`awk -F' *= *' '{ if ($1 == "powergraph.home") print $2 }' $config/powergraph.properties`
fi

if [ -z $POWERGRAPH_HOME ]; then
    echo "Error: home directory for GraphMat not specified."
    echo "Define the environment variable \$POWERGRAPH_HOME or modify powergraph.home in $config/powergraph.properties"
    exit 1
fi

if [ -z $DISABLE_MPI ]; then
    DISABLE_MPI=`awk -F' *= *' '{ if ($1 == "powergraph.disable_mpi") print $2 }' $config/powergraph.properties`
fi

DISABLE_JVM=1

mkdir -p bin
(cd bin && cmake -DCMAKE_BUILD_TYPE=Release ../src/ -DPOWERGRAPH_HOME=$POWERGRAPH_HOME -DNO_JVM=$DISABLE_JVM -DNO_MPI=$DISABLE_MPI && make all)

if [ $? -ne 0 ]
then
    echo "compilation failed"
    exit 1
fi
