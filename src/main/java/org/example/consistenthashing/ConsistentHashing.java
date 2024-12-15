package org.example.consistenthashing;

import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.example.node.Node;

public class ConsistentHashing {
    private Node[] nodes; 
    private Bucket[] buckets;
    private Set<Bucket> manageBucket;

    public ConsistentHashing(Node[] nodes, Node root) {
        this.nodes = nodes; 
        this.buckets = new Bucket[nodes.length]; 
        this.manageBucket = new java.util.HashSet<Bucket>();
        for (int i = 0; i < nodes.length; i++) {
            this.buckets[i] = new Bucket(this.nodes[i]); 

            if (nodes[i].getPort() == root.getPort())
                this.manageBucket.add(this.buckets[i]);
        }
    }

    private HashCode sha256(String key) {
        return Hashing.sha256()
            .hashString(key, Charsets.UTF_8);
    }

    public boolean manageKey(String key) {
        int bucketId = Hashing.consistentHash(sha256(key), this.buckets.length);
        return this.manageBucket.contains(this.buckets[bucketId]);
    }

    public Node getNodeManage(String key) {
        int bucketId = Hashing.consistentHash(sha256(key), this.buckets.length);
        return this.buckets[bucketId].getNode();
    }
}

