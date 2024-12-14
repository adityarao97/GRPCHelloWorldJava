package org.example.grpc.PeerToPeer;

import com.example.grpc.GreeterGrpc;
import com.example.grpc.HelloWorldProto;
import com.example.grpc.PeerToPeerGrpc;
import com.example.grpc.PeerToPeerProto;
import com.example.grpc.PeerToPeerGrpc.PeerToPeerBlockingStub;
import com.example.grpc.PeerToPeerGrpc.PeerToPeerImplBase;
import com.example.grpc.PeerToPeerGrpc.PeerToPeerStub;
import com.example.grpc.PeerToPeerProto.ForwardRequest;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.example.forward.ForwardMessage;
import org.example.forward.PeerConnection;
import org.example.node.Node;

public class PeerToPeerApplication {
    public static void main(String[] args) throws IOException, InterruptedException {
        int serverPort = Integer.parseInt(args[0]);
        int peerPort = Integer.parseInt(args[1]);
        Server server = ServerBuilder.forPort(serverPort)
                .addService(new PeerToPeerImpl())
                .build();

        server.start();
        System.out.println("Server started at port " + serverPort);

        //
        int[] peerPorts = new int[1];
        peerPorts[0] = peerPort;
        Node peerNode = new Node(peerPort);

        BlockingQueue<ForwardMessage> queue = new ArrayBlockingQueue<>(1000);
        PeerConnection connection = new PeerConnection(peerPorts, serverPort, queue);

        Thread producer = new Thread() {
            public void run() {
                int counter = 0;
                while (true) {
                    try {
                        counter++;
                        Thread.sleep(1000);
                        queue.put(new ForwardMessage("key " + serverPort, "value " + counter, peerNode));
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        producer.start();

        //connection.ForwardMessage(args[0], "This is message from" + args[0], peerPort);
        server.awaitTermination();
    }

    /*static class Address {
        private int port;
        public Address(int port) {
            this.port = port;
        }   

        public int getPort() {
            return this.port;
        }

        @Override
        public boolean equals(Object obj) {
            // TODO Auto-generated method stub
            if (obj == null) {
                return false;
            }

            if (obj instanceof Address) {
                Address other = (Address) obj;
                return this.port == other.port;
            }

            return false;
        }
    }*/

    /*static class ForwardMessage {
        private String key;
        private String value;
        private Address destination;
        public ForwardMessage(String key, String value, Address dest) {
            this.key = key;
            this.value = value;
            this.destination = dest;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }   
         
        public Address getDestination() {
            return this.destination;
        }
    }

    static class PeerConnection {
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
    }*/

    /*static class PeerToPeerImpl extends PeerToPeerImplBase {
        @Override
        public void forward(PeerToPeerProto.ForwardRequest request, StreamObserver<PeerToPeerProto.ForwardResponse> responseObserver) {
            String message = "receive forwarding message: " + request.getKey() + " " + request.getValue();
            System.out.println(message);
            PeerToPeerProto.ForwardResponse reply = PeerToPeerProto.ForwardResponse.newBuilder().setMessage(message).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }*/
} 