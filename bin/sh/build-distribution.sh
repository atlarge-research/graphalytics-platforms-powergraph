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

# Adapt this build script to your cluster environment.

# Download Powergraph v2.2 #a038f97
git clone https://github.com/jegonzal/PowerGraph

# Move into the powergraph repo directory.
cd PowerGraph

# Patch the Powergraph CMakeLists.txt to change dependency lines containing broken URIs.
# Replace the hash in the patch name with any newer version for which a patch file exists,
# or when this is fixed, comment out the patch entirely.
patch CMakeLists.txt < ../$(dirname "$0")/../utils/CMakeLists_a038f97.diff

# Adapt compilation setup
rm -rf deps/*
./configure --no_jvm

# Load compilers
module unload intel-mpi
module unload gcc
module load openmpi/open64/64/1.10.1
module load openmpi/gcc/64/1.10.1
module list

# Compile Distribution
cd release
make
