package com.ToniC.players.Dijkstra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {

    private List<Node> nodes = new ArrayList<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}