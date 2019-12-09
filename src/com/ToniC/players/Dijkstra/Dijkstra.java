package com.ToniC.players.Dijkstra;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Dijkstra {

    public static void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies)
            {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);

                    v.minDistance = distanceThroughU ;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public static List<Point> getShortestPathTo(Vertex target)
    {
        List<Point> path = new ArrayList();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex.name);

        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args)
    {
        // mark all the vertices
        Vertex A = new Vertex(new Point(1,1));
        Vertex B = new Vertex(new Point(1,1));
        Vertex D = new Vertex(new Point(1,1));
        Vertex F = new Vertex(new Point(1,1));
        Vertex K = new Vertex(new Point(1,1));
        Vertex J = new Vertex(new Point(1,1));
        Vertex M = new Vertex(new Point(1,1));
        Vertex O = new Vertex(new Point(1,1));
        Vertex P = new Vertex(new Point(1,1));
        Vertex R = new Vertex(new Point(1,1));
        Vertex Z = new Vertex(new Point(1,1));

        // set the edges and weight
//        A.adjacencies = new Edge[]{ new Edge(M, 8) };
//        B.adjacencies = new Edge[]{ new Edge(D, 11) };
//        D.adjacencies = new Edge[]{ new Edge(B, 11) };
//        F.adjacencies = new Edge[]{ new Edge(K, 23) };
//        K.adjacencies = new Edge[]{ new Edge(O, 40) };
//        J.adjacencies = new Edge[]{ new Edge(K, 25) };
//        M.adjacencies = new Edge[]{ new Edge(R, 8) };
//        O.adjacencies = new Edge[]{ new Edge(K, 40) };
//        P.adjacencies = new Edge[]{ new Edge(Z, 18) };
//        R.adjacencies = new Edge[]{ new Edge(P, 15) };
//        Z.adjacencies = new Edge[]{ new Edge(P, 18) };
//

//        computePaths(A); // run Dijkstra
//        System.out.println("Distance to " + Z + ": " + Z.minDistance);
//        List path = getShortestPathTo(Z);
//        System.out.println("Path: " + path);
    }
}