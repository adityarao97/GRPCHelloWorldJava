package org.example.consistenthashing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConsistentHashingTest {
    private ConsistentHashing consistentHashing;

    @BeforeEach
    public void setUp() {
        Node[] ports = {new Node(8080), new Node(8081), new Node(8082)};
        Node rootPort = new Node(8080);
        consistentHashing = new ConsistentHashing(ports, rootPort);
        System.err.println("setUp: consistentHashing initialized");
    }

    @Test
    public void testManageKey() {
        String key = "testKey";
        assertNotNull(consistentHashing);
        System.err.println(consistentHashing.manageKey(key));
        assertFalse(consistentHashing.manageKey(key) | consistentHashing.manageKey(key));
    }

    @Test
    public void testGetNodeManage() {
        String key = "testKey";
        Node node = consistentHashing.getNodeManage(key);
        assertNotNull(node);
        assertTrue(node.getPort() == 8080 || node.getPort() == 8081 || node.getPort() == 8082);
    }

    @Test
    public void testGetNodeManageSameKey() {
        String key = "testKey";
        Node node1 = consistentHashing.getNodeManage(key);
        Node node2 = consistentHashing.getNodeManage(key);
        assertTrue(node1.equals(node2));
    }
}