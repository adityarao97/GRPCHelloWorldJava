package org.example.forward;
import org.example.node.Node;

public class ForwardMessage {
    private String key;
    private String value;
    private Node destination;
    public ForwardMessage(String key, String value, Node dest) {
        this.key = key;
        this.value = value;
        this.destination = dest;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }   
     
    public Node getDestination() {
        return this.destination;
    }
}
