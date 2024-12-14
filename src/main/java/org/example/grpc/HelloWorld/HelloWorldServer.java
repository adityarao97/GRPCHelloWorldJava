package org.example.grpc.HelloWorld;

import com.example.grpc.GreeterGrpc;
import com.example.grpc.HelloWorldProto;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class HelloWorldServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8080)
                .addService(new GreeterImpl())
                .build();

        System.out.println("Server started at port 8080");
        server.start();
        server.awaitTermination();
    }

    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloWorldProto.HelloRequest request, StreamObserver<HelloWorldProto.HelloReply> responseObserver) {
            String message = "Hello, " + request.getName();
            HelloWorldProto.HelloReply reply = HelloWorldProto.HelloReply.newBuilder().setMessage(message).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}