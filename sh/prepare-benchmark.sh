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

# Set Library jar
export LIBRARY_JAR=`ls lib/graphalytics-*default*.jar`
GRANULA_ENABLED=$(grep -E "^benchmark.run.granula.enabled[	 ]*[:=]" $config/granula.properties | sed 's/benchmark.run.granula.enabled[\t ]*[:=][\t ]*\([^\t ]*\).*/\1/g' | head -n 1)
if [ "$GRANULA_ENABLED" = "true" ] ; then
 if ! find lib -name "graphalytics-*granula*.jar" | grep -q '.'; then
    echo "Granula cannot be enabled due to missing library jar" >&2
 else
    export LIBRARY_JAR=`ls lib/graphalytics-*granula*.jar`
 fi
fi

# Ensure the configuration file exists
if [ ! -f "$config/platform.properties" ]; then
	echo "Missing mandatory configuration file: $config/platform.properties" >&2
	exit 1
fi


# Build binaries
if [ -z $POWERGRAPH_HOME ]; then
    POWERGRAPH_HOME=`awk -F' *= *' '{ if ($1 == "platform.powergraph.home") print $2 }' $config/platform.properties`
fi

if [ -z $POWERGRAPH_HOME ]; then
    echo "Error: home directory for PowerGraph not specified."
    echo "Define the environment variable \$POWERGRAPH_HOME or modify platform.powergraph.home in $config/platform.properties"
    exit 1
fi

if [ -z $DISABLE_MPI ]; then
    DISABLE_MPI=`awk -F' *= *' '{ if ($1 == "platform.powergraph.disable_mpi") print $2 }' $config/platform.properties`
fi

DISABLE_JVM=1

mkdir -p bin/standard
(cd bin/standard && cmake -DCMAKE_BUILD_TYPE=Release ../../src/main/c -DPOWERGRAPH_HOME=$POWERGRAPH_HOME -DNO_JVM=$DISABLE_JVM -DNO_MPI=$DISABLE_MPI && make all)

if [ "$GRANULA_ENABLED" = "true" ] ; then
 mkdir -p bin/granula
 (cd bin/granula && cmake -DCMAKE_BUILD_TYPE=Release -DGRANULA=1 ../../src/main/c -DPOWERGRAPH_HOME=$POWERGRAPH_HOME -DNO_JVM=$DISABLE_JVM -DNO_MPI=$DISABLE_MPI && make all)
fi


if [ $? -ne 0 ]
then
    echo "compilation failed"
    exit 1
fi
