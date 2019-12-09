package com.ToniC.players.Dijkstra;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable
{
    public final Point name;
    public List<Edge> adjacencies = new ArrayList<>();
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public Vertex(Point argName) { name = argName; }
    public String toString() { return name.toString(); }

    @Override
    public int compareTo(Object o) {
        return Double.compare(minDistance, ((Vertex) o).minDistance);
    }
}


