#!/bin/bash

n_server=$1
workload=$2
virtual_node=$3

echo $n_server

start_server=8912
end_server=$((start_server + n_server - 1))

servers=$(seq $start_server $end_server)
echo $servers

servers_array=($servers)
servers_str=$(IFS=,; echo "${servers_array[*]}")
echo $servers_str

virtual_servers_array=()
for server in $(seq $start_server $end_server); do
    for ((i=0; i<$virtual_node; i++)); do
        virtual_servers_array+=($server)
    done
done

echo ${virtual_servers_array[@]}

virtual_servers_array=($(shuf -e "${virtual_servers_array[@]}"))
echo "Shuffled servers"
echo ${virtual_servers_array[@]}

virtual_servers_str=$(IFS=,; echo "${virtual_servers_array[*]}")
echo $virtual_servers_str

pids=()
for port in ${servers[@]}; do
    ./gradlew runServer --args="$port $virtual_servers_str" &
    pids+=($!)
done

./gradlew runClient --args="$servers_str $workload" &
pids+=($!)

trap 'kill "${pids[@]}"; exit' SIGINT
wait ${pids[@]}

echo "Server started with pids: ${pids[@]}"
