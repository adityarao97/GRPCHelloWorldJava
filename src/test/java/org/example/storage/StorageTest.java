package org.example.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.example.consistenthashing.ConsistentHashing;
import org.example.forward.ForwardMessage;
import org.example.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StorageTest {
    private Storage storage;
    private BlockingQueue<StorageRequest> requestQueue;
    private BlockingQueue<ForwardMessage> forwardQueue;
    private ConsistentHashing consistentHashing;

    @BeforeEach
    public void setUp() {
        requestQueue = new ArrayBlockingQueue<>(10);
        forwardQueue = new ArrayBlockingQueue<>(10);
        consistentHashing = mock(ConsistentHashing.class);
        storage = new Storage(requestQueue, forwardQueue, consistentHashing);
    }

    @Test
    public void testRunValidRequestManagedByThisNode() throws InterruptedException {
        StorageRequest request = new StorageRequest("key1", "value1");
        when(consistentHashing.manageKey("key1")).thenReturn(true);

        requestQueue.put(request);
        Thread.sleep(100); // Give some time for the storage thread to process

        assertEquals(1, storage.getSize());
        assertEquals("value1", storage.kv.get("key1"));
    }

    @Test
    public void testRunValidRequestNotManagedByThisNode() throws InterruptedException {
        StorageRequest request = new StorageRequest("key2", "value2");
        Node node = new Node(8081);
        when(consistentHashing.manageKey("key2")).thenReturn(false);
        when(consistentHashing.getNodeManage("key2")).thenReturn(node);

        requestQueue.put(request);
        Thread.sleep(100); // Give some time for the storage thread to process

        assertEquals(0, storage.getSize());
        ForwardMessage message = forwardQueue.take();
        assertEquals("key2", message.getKey());
        assertEquals("value2", message.getValue());
        assertEquals(node, message.getDestination());
    }

    @Test
    public void testRunInvalidRequest() throws InterruptedException {
        StorageRequest request = mock(StorageRequest.class);
        when(request.isValid()).thenReturn(false);

        requestQueue.put(request);
        Thread.sleep(100); // Give some time for the storage thread to process

        assertEquals(0, storage.getSize());
        assertEquals(0, forwardQueue.size());
    }
}