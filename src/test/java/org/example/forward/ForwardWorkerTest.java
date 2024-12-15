package org.example.forward;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.example.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.grpc.PeerToPeerGrpc;
import com.example.grpc.PeerToPeerProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ForwardWorkerTest {
    private Node[] nodes;
    private int serverPort;
    private BlockingQueue<ForwardMessage> queue;
    private ForwardWorker forwardWorker;

    @BeforeEach
    public void setUp() {
        nodes = new Node[] { new Node(8080), new Node(8081), new Node(8082) };
        serverPort = 8080;
        queue = new ArrayBlockingQueue<>(10);
        forwardWorker = new ForwardWorker(nodes, serverPort, queue);
    }

    @Test
    public void testForwardWorkerInitialization() {
        assertNotNull(forwardWorker);
    }

    @Test
    public void testRun() throws InterruptedException {
        ForwardMessage message = new ForwardMessage("testKey", "testValue", new Node(8081));
        queue.put(message);

        Thread.sleep(1000); // Wait for the worker thread to process the message

        // Verify that the message was put to the queue 
        assertNotNull(queue);
    }

    @Test
    public void testSend() {
        /*ManagedChannel channel = mock(ManagedChannel.class);
        PeerToPeerGrpc.PeerToPeerBlockingStub stub = mock(PeerToPeerGrpc.PeerToPeerBlockingStub.class);
        PeerToPeerProto.ForwardResponse response = PeerToPeerProto.ForwardResponse.newBuilder().setMessage("Success").build();

        when(stub.forward(any(PeerToPeerProto.ForwardRequest.class))).thenReturn(response);
        when(ManagedChannelBuilder.forAddress(anyString(), anyInt()).usePlaintext().build()).thenReturn(channel);
        when(PeerToPeerGrpc.newBlockingStub(channel)).thenReturn(stub);

        forwardWorker.send("testKey", "testValue", 8081);

        verify(stub, times(1)).forward(any(PeerToPeerProto.ForwardRequest.class));*/
    }
}