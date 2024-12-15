package org.example.grpc.Client;

import org.checkerframework.checker.units.qual.s;
import org.example.node.Node;
import org.example.storage.StorageRequest;

import com.example.grpc.StoreServiceGrpc;
import com.example.grpc.StorageProto.PutRequest;
import com.example.grpc.StorageProto.PutResponse;
import com.example.grpc.StorageProto.StorageSizeRequest;
import com.example.grpc.StorageProto.StorageSizeResponse;
import com.example.grpc.StoreServiceGrpc.StoreServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class client {
    
    public static PutRequest[] generateWorkLoad(int workloadSize) {
        PutRequest[] workload = new PutRequest[workloadSize];
        for (int i = 1; i <= workloadSize; i++) {
            workload[i] = PutRequest.newBuilder()
                    .setKey("key-" + i)
                    .setValue("value-" + i)
                    .build();
        }
        return workload;
    }

    public static void main(String[] args) {
        // Create a channel to the server
        // args[0] is comma separated list of server ports
        String[] serverPorts = args[0].split(",");
        Node[] servers = Node.createNodes(serverPorts);
        ManagedChannel[] channel = new ManagedChannel[servers.length];
        StoreServiceBlockingStub[] stub = new StoreServiceBlockingStub[servers.length];

        for (int i = 0; i < servers.length; i++) {
            channel[i] = ManagedChannelBuilder.forAddress("localhost", servers[i].getPort())
                    .usePlaintext()
                    .build();
            stub[i] = StoreServiceGrpc.newBlockingStub(channel[i]).withWaitForReady();
        }

        // send requests to the server
        PutRequest[] workLoad = generateWorkLoad(10);
        for (int i = 0; i < workLoad.length; i++) {
            PutRequest request = workLoad[i];
            int serverIndex = i % servers.length;
            PutResponse response = stub[serverIndex].put(request);
            System.out.println("Response from server: " + response.getMsg());
        }

        // get the storage size from the server 
        for (int i = 0; i < servers.length; i++) {
            StorageSizeResponse response = stub[i].getStorageSize(StorageSizeRequest.newBuilder().build());
            System.out.println("Response from server: " + servers[i].getPort() + " size: " + response.getStorageSize());
        }
        //System.out.println("Response from server: " + response.getMessage());
    }
}
