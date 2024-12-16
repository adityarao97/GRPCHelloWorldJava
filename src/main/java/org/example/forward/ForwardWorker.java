package org.example.forward;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import org.example.node.Node;

import com.example.grpc.PeerToPeerGrpc;
import com.example.grpc.PeerToPeerGrpc.PeerToPeerBlockingStub;
import com.example.grpc.PeerToPeerProto;

import io.grpc.ManagedChannelBuilder;

public class ForwardWorker{
    private Node[] nodes;
    private int serverPort;
    private PeerToPeerBlockingStub[] stubs;
    private BlockingQueue<ForwardMessage> queue;

    //init logging
    private static final Logger logger = Logger.getLogger(ForwardWorker.class.getName());

    public ForwardWorker(Node[] nodes, int serverPort, BlockingQueue<ForwardMessage> queue) {
        this.nodes = nodes;
        this.serverPort = serverPort;
        this.queue = queue;
        this.stubs = new PeerToPeerBlockingStub[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            this.stubs[i] = PeerToPeerGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", this.nodes[i].getPort())
                    .usePlaintext()
                    .build()).withWaitForReady();
        }   
    
        Thread t = new Thread() {
            public void run() {
                ForwardWorker.this.run();
            }
        };
        t.start();
    }

    private void run() {
        System.out.println("Peer connection started");
        while (true) {
            ForwardMessage message;
            try {
                message = queue.take();
                send(message.getKey(), message.getValue(), message.getDestination().getPort());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void send(String key, String value, int to) {
        int index = -1;
        for (int i = 0; i < this.nodes.length; i++) {
            if (this.nodes[i].getPort() == to) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("Peer not found");
            return;
        }

        logger.info(String.format("Forward key %s to peer: %d", key, to));
        PeerToPeerProto.ForwardRequest request = PeerToPeerProto.ForwardRequest.newBuilder().setKey(key).setValue(value).build();
        PeerToPeerProto.ForwardResponse response = this.stubs[index].forward(request);
        //System.out.println(response.getMessage()); 
    } 
}
