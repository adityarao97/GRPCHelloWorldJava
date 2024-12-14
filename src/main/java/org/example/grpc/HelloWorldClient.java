package org.example.grpc;

import com.example.grpc.Mini3Proto;
import com.example.grpc.StoreServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldClient {
    public static void main(String[] args) {
        ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        ManagedChannel channel2 = ManagedChannelBuilder.forAddress("localhost", 8081)
                .usePlaintext()
                .build();

//        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
        StoreServiceGrpc.StoreServiceBlockingStub storeService1 = StoreServiceGrpc.newBlockingStub(channel1);
        StoreServiceGrpc.StoreServiceBlockingStub storeService2 = StoreServiceGrpc.newBlockingStub(channel2);

//        HelloWorldProto.HelloRequest request = HelloWorldProto.HelloRequest.newBuilder().setName("Aditya!").build();
//        HelloWorldProto.HelloReply reply = stub.sayHello(request);

        HashMap<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        Mini3Proto.PutMessageRequest requestPutMessage = putMapValue(map);
        Mini3Proto.PutMessageResponse putMessageResponse = storeService1.putMessage(requestPutMessage);
        System.out.println("map value from server 1 is " + putMessageResponse.toString());


        HashMap<String, String> map2 = new HashMap<>();
        map2.put("key2", "value3");
        map2.put("key3", "value3");
        Mini3Proto.PutMessageRequest requestPutMessage2 = putMapValue(map2);
        Mini3Proto.PutMessageResponse putMessageResponse2 = storeService2.putMessage(requestPutMessage2);
        System.out.println("map value from server 2 is " + putMessageResponse2.toString());

        Mini3Proto.EmptyInput requestEmptyInput = Mini3Proto.EmptyInput.newBuilder().build();
        Mini3Proto.GetKeySizeResponse getKeySizeResponse1 = storeService1.getKeySize(requestEmptyInput);
        Mini3Proto.GetKeySizeResponse getKeySizeResponse2 = storeService2.getKeySize(requestEmptyInput);
        System.out.println("server 1 map key size is : " + getKeySizeResponse1.getGetKeySizeResponse());
        System.out.println("server 2 map key size is : " + getKeySizeResponse2.getGetKeySizeResponse());
//        System.out.println(reply.getMessage());

        channel1.shutdown();
        channel2.shutdown();
    }

    private static Mini3Proto.PutMessageRequest putMapValue(HashMap<String, String> map) {
        return Mini3Proto.PutMessageRequest.newBuilder().putAllMap(map).build();
    }
}
