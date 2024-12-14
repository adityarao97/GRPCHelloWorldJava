package org.example.node;

public class Node {
    private int port;
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
}
