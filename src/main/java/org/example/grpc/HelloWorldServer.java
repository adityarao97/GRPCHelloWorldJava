package org.example.grpc;

import com.example.grpc.GreeterGrpc;
import com.example.grpc.Mini3Proto;
import com.example.grpc.StoreServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.HashMap;

public class HelloWorldServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server1 = ServerBuilder.forPort(8080)
                .addService(new StoreServiceImpl())
                .build();

        Server server2 = ServerBuilder.forPort(8081)
                .addService(new StoreServiceImpl())
                .build();

        System.out.println("Server1 started at port 8080");
        System.out.println("Server2 started at port 8081");
        server1.start();
        server2.start();
        server1.awaitTermination();
        server2.awaitTermination();
    }

    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(Mini3Proto.HelloRequest request, StreamObserver<Mini3Proto.HelloReply> responseObserver) {
            String message = "Hello, " + request.getName();
            Mini3Proto.HelloReply reply = Mini3Proto.HelloReply.newBuilder().setMessage(message).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    static class StoreServiceImpl extends StoreServiceGrpc.StoreServiceImplBase {
        HashMap<String, String> map = new HashMap<>();
        @Override
        public void putMessage(Mini3Proto.PutMessageRequest request, StreamObserver<Mini3Proto.PutMessageResponse> responseObserver) {
            map.putAll(request.getDefaultInstanceForType().getMapMap());
            Mini3Proto.PutMessageResponse response = Mini3Proto.PutMessageResponse.newBuilder().putAllPutResponse(map).build();
            System.out.println("Server map value is : " + map.toString());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void getKeySize(Mini3Proto.EmptyInput emptyInput, StreamObserver<Mini3Proto.GetKeySizeResponse> responseObserver) {
            System.out.println("Key size is : " + map.size());
            Mini3Proto.GetKeySizeResponse response = Mini3Proto.GetKeySizeResponse.newBuilder().setGetKeySizeResponse(map.size()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}