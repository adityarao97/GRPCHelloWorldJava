package org.example.forward;

import java.util.concurrent.BlockingQueue;

import com.example.grpc.PeerToPeerGrpc;
import com.example.grpc.PeerToPeerGrpc.PeerToPeerBlockingStub;
import com.example.grpc.PeerToPeerProto;

import io.grpc.ManagedChannelBuilder;

public class PeerConnection {
    private int[] ports;
    private int serverPort;
    private PeerToPeerBlockingStub[] stubs;
    private BlockingQueue<ForwardMessage> queue;

    public PeerConnection(int[] ports, int serverPort, BlockingQueue<ForwardMessage> queue) {
        this.ports = ports;
        this.serverPort = serverPort;
        this.queue = queue;
        this.stubs = new PeerToPeerBlockingStub[ports.length];
        for (int i = 0; i < ports.length; i++) {
            this.stubs[i] = PeerToPeerGrpc.newBlockingStub(ManagedChannelBuilder.forAddress("localhost", ports[i])
                    .usePlaintext()
                    .build()).withWaitForReady();
        }   
    
        Thread t = new Thread() {
            public void run() {
                PeerConnection.this.run();
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
        for (int i = 0; i < this.ports.length; i++) {
            if (this.ports[i] == to) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("Peer not found");
            return;
        }

        System.out.println("Forwarding message to peer: " + to);
        PeerToPeerProto.ForwardRequest request = PeerToPeerProto.ForwardRequest.newBuilder().setKey(key).setValue(value).build();
        PeerToPeerProto.ForwardResponse response = this.stubs[index].forward(request);
        //System.out.println(response.getMessage()); 
    } 
}
