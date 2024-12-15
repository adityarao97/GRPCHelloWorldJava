package org.example.application;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.example.consistenthashing.ConsistentHashing;
import org.example.forward.ForwardMessage;
import org.example.forward.ForwardWorker;
import org.example.grpc.PeerToPeer.PeerToPeerImpl;
import org.example.grpc.StoreService.StoreServiceImpl;
import org.example.node.Node;
import org.example.storage.Storage;
import org.example.storage.StorageRequest;

import io.grpc.ServerBuilder;
import io.grpc.Server;

public class Application {
    public static void main(String[] args) throws IOException, InterruptedException {
        int serverPort = Integer.parseInt(args[0]);
        String[] peerPorts = args[1].split(",");

        Node[] peerNodes = Node.createNodes(peerPorts);
        Node serverNode = new Node(serverPort);

        BlockingQueue<ForwardMessage> forwardQueue = new ArrayBlockingQueue<>(1000);
        BlockingQueue<StorageRequest> storageQueue = new ArrayBlockingQueue<>(1000);
        ConsistentHashing consistentHashing = new ConsistentHashing(peerNodes, serverNode);

        // start worker inside constructor
        Storage storage = new Storage(storageQueue, forwardQueue, consistentHashing);
        ForwardWorker forwardWorker = new ForwardWorker(peerNodes, serverPort, forwardQueue);

        Server server = ServerBuilder.forPort(serverPort)
                .addService(new PeerToPeerImpl(storageQueue))
                .addService(new StoreServiceImpl(storageQueue, storage))
                .build();

        server.start();
        System.out.println("Server started at port " + serverPort);
    }
}
