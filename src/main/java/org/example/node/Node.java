package org.example.node;

public class Node {
    private int port;
    public Node(String port) {
        this.port = Integer.parseInt(port);
    }
    
    public Node(int port) {
        this.port = port;
    }     

    public int getPort() {
        return this.port;
    }   

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if (obj == null) {
            return false;
        }
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return this.port == other.port;
        }
        return false;
    }

    public static Node[] createNodes(int[] ports) {
        Node[] nodes = new Node[ports.length];
        for (int i = 0; i < ports.length; i++) {
            nodes[i] = new Node(ports[i]);
        }
        return nodes;
    }

    public static Node[] createNodes(String[] ports) {
        Node[] nodes = new Node[ports.length];
        for (int i = 0; i < ports.length; i++) {
            nodes[i] = new Node(ports[i]);
        }
        return nodes;
    }
}
