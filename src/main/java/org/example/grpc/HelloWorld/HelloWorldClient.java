package org.example.grpc.HelloWorld;

import com.example.grpc.GreeterGrpc;
import com.example.grpc.HelloWorldProto;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HelloWorldClient {
    public static void main(String[] args) {
        ManagedChannel channel = null;
        
        channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();
        
        System.out.println("Channel state: " + channel.getState(false));

        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel).withWaitForReady();

        HelloWorldProto.HelloRequest request = HelloWorldProto.HelloRequest.newBuilder().setName("Aditya!").build();
        HelloWorldProto.HelloReply reply = stub.sayHello(request);

        System.out.println(reply.getMessage());

        channel.shutdown();
    }
}
