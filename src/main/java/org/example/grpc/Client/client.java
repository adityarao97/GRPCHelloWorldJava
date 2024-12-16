package org.example.grpc.client;

import org.example.node.Node;

import com.example.grpc.StoreServiceGrpc;
import com.example.grpc.StorageProto.PutRequest;
import com.example.grpc.StorageProto.PutResponse;
import com.example.grpc.StorageProto.StorageSizeRequest;
import com.example.grpc.StorageProto.StorageSizeResponse;
import com.example.grpc.StoreServiceGrpc.StoreServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Client {
    
    public static PutRequest[] generateWorkLoad(int workloadSize) {
        PutRequest[] workload = new PutRequest[workloadSize];
        for (int i = 0; i < workloadSize; i++) { // Changed from i = 1 to i = 0
            workload[i] = PutRequest.newBuilder()
                    .setKey("key-" + (i + 1)) // Adjusted key to start from 1
                    .setValue("value-" + (i + 1)) // Adjusted value to start from 1
                    .build();
        }
        return workload;
    }

    public static PutRequest[] generateRandomWorkload(int workloadSize) {
        PutRequest[] workload = new PutRequest[workloadSize];
        for (int i = 0; i < workloadSize; i++) { // Changed from i = 1 to i = 0
            workload[i] = PutRequest.newBuilder()
                    .setKey(java.util.UUID.randomUUID().toString()) // Generate random key
                    .setValue("value-" + (i + 1)) // Adjusted value to start from 1
                    .build();
        }
        return workload;
    }

    public static void main(String[] args) {
        // Create a channel to the server
        // args[0] is comma separated list of server ports
        int worklLoadSize = Integer.parseInt(args[1]);

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

        System.out.println("Client started");
        // send requests to the server
        PutRequest[] workLoad = generateRandomWorkload(worklLoadSize);
        for (int i = 0; i < workLoad.length; i++) {
            PutRequest request = workLoad[i];
            int serverIndex = i % servers.length;
            PutResponse response = stub[serverIndex].put(request);
            System.out.println("Response from server: " + response.getMsg());
        }

        // sleep for 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // get the storage size from the server 
        for (int i = 0; i < servers.length; i++) {
            StorageSizeResponse response = stub[i].getStorageSize(StorageSizeRequest.newBuilder().build());
            System.out.println("Response from server: " + servers[i].getPort() + " size: " + response.getStorageSize());
        }
        //System.out.println("Response from server: " + response.getMessage());
    }
}
