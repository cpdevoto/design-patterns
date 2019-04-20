#!/usr/bin/env sh

cd /opt/apache-flume

echo "Starting Apache Flume"

bin/flume-ng agent --conf conf --conf-file conf/flume.conf --name agent
