# Build project
```sh
./gradlew clean install
```

# Run the client
```sh
# args is a list of server
./gradlew runClient --args="8912,8913"

```

# Run the server
```sh
# args are the root server port and a list of server
./gradlew runServer --args="8912 8912,8913"

# in another terminal, run another server
./gradlew runServer --args="8913 8912,8913"
```

# Run the benchmark
```sh
./scripts/benchmark_virtual_node.sh ${n_nodes} ${worload_size} ${virtual_node}
```
