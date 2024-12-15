package org.example.grpc.PeerToPeer;

import java.util.concurrent.BlockingQueue;

import org.example.storage.StorageRequest;

import com.example.grpc.PeerToPeerGrpc.PeerToPeerImplBase;
import com.example.grpc.PeerToPeerProto.ForwardRequest;
import com.example.grpc.PeerToPeerProto.ForwardResponse;

import io.grpc.stub.StreamObserver;

public class PeerToPeerImpl extends PeerToPeerImplBase {
    private BlockingQueue<StorageRequest> requestQueue;
    public PeerToPeerImpl() {
        this.requestQueue = null;
    }   

    public PeerToPeerImpl(BlockingQueue<StorageRequest> requestQueue) {
        this.requestQueue = requestQueue;
    }

    @Override
    public void forward(ForwardRequest request, StreamObserver<ForwardResponse> responseObserver) {
        String message = "receive forwarding message: " + request.getKey() + " " + request.getValue();
        System.out.println(message);
        // push message to queue
        if (this.requestQueue != null) { 
            this.requestQueue.add(new StorageRequest(request.getKey(), request.getValue()));
        }

        ForwardResponse reply = ForwardResponse.newBuilder().setMessage(message).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}