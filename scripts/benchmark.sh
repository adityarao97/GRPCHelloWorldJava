#!/bin/bash

n_server=$1
workload=$2

echo $n_server

start_server=8912
end_server=$((start_server + n_server - 1))

servers=$(seq $start_server $end_server)
echo $servers

servers_array=($servers)
servers_str=$(IFS=,; echo "${servers_array[*]}")
echo $servers_str

pids=()
for port in ${servers_array[@]}; do
    ./gradlew runServer --args="$port $servers_str" &
    pids+=($!)
done

./gradlew runClient --args="$servers_str $workload" &
pids+=($!)

trap 'kill "${pids[@]}"; exit' SIGINT
wait ${pids[@]}

echo "Server started with pids: ${pids[@]}"
