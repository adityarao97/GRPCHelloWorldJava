package org.example.consistenthashing;

import org.example.node.Node;

public class Bucket {
    private Node node;
    public Bucket(Node node) {
        this.node = node;
    }
   
    public Node getNode() {
        return this.node;
    }
}
