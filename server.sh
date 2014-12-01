#!/bin/sh

cd $(dirname $0)
export LD_LIBRARY_PATH=./bin/encryption/
java -cp ./bin/:./lib/* Server $@
