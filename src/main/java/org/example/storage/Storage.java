package org.example.storage;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import org.example.consistenthashing.ConsistentHashing;
import org.example.forward.ForwardMessage;

public class Storage {
    HashMap<String, String> kv; 
    BlockingQueue<StorageRequest> requestQueue;
    BlockingQueue<ForwardMessage> forwardQueue;
    ConsistentHashing consistentHashing;

    public Storage() {
        this.kv = new HashMap<String, String>();
    }

    public Storage(BlockingQueue<StorageRequest> requestQueue, BlockingQueue<ForwardMessage> forwardQueue, ConsistentHashing consistentHashing) {
        this.kv = new HashMap<String, String>();
        this.consistentHashing = consistentHashing;
        this.requestQueue = requestQueue;
        this.forwardQueue = forwardQueue;

        Thread t = new Thread() {
            public void run() {
                Storage.this.run();
            }
        };
        t.start();
    }

    public int getSize() {
        return this.kv.size();
    } 

    private void run() {
        while (true) {
            try {
                StorageRequest request = this.requestQueue.take();
                if (!request.isValid()) {
                    System.err.println("Invalid request");
                    continue;
                }
                if (consistentHashing.manageKey(request.getKey())) {
                    this.kv.put(request.getKey(), request.getValue());
                } else {
                    ForwardMessage message = new ForwardMessage(request.getKey(), request.getValue(), consistentHashing.getNodeManage(request.getKey()));
                    this.forwardQueue.put(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
