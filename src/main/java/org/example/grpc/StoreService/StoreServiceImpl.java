package org.example.grpc.StoreService;

import java.util.concurrent.BlockingQueue;

import org.example.storage.Storage;
import org.example.storage.StorageRequest;

import com.example.grpc.StorageProto.PutRequest;
import com.example.grpc.StorageProto.PutResponse;
import com.example.grpc.StorageProto.StorageSizeRequest;
import com.example.grpc.StorageProto.StorageSizeResponse;
import com.example.grpc.StoreServiceGrpc.StoreServiceImplBase;

import io.grpc.stub.StreamObserver;

public class StoreServiceImpl extends StoreServiceImplBase {
    BlockingQueue<StorageRequest> requestQueue;
    Storage storage;

    public StoreServiceImpl() {
        this.requestQueue = null;
    }

    public StoreServiceImpl(BlockingQueue<StorageRequest> requestQueue, Storage storage) {
        this.requestQueue = requestQueue;
        this.storage = storage;
    }

    @Override
    public void put(PutRequest request, StreamObserver<PutResponse> responseObserver) {
        System.out.println("Received request: " + request.getKey() + " -> " + request.getValue());
        if (this.requestQueue != null) {
            this.requestQueue.add(new StorageRequest(request.getKey(), request.getValue()));
        }
        responseObserver.onNext(PutResponse.newBuilder().setMsg("Put request received key " + request.getKey()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getStorageSize(StorageSizeRequest request, StreamObserver<StorageSizeResponse> responseObserver) {
        // this may be not a thread-safe solution
        responseObserver.onNext(StorageSizeResponse.newBuilder().setStorageSize(this.storage.getSize()).build());
        responseObserver.onCompleted();
    }
}