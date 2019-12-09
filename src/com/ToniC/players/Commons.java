package com.ToniC.players;

import com.ToniC.players.Dijkstra.Graph;
import com.ToniC.players.Dijkstra.Node;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import javafx.geometry.Point3D;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Commons {

    private List<Point> neighbor_directions = Arrays.asList(
            new Point(1, 0), new Point(1, -1), new Point(0, -1),
            new Point(-1, 0), new Point(-1, 1), new Point(0, 1));
    private List<Point> disgonal_neighbor_directions = Arrays.asList(
            new Point(2, -1), new Point(1, 1), new Point(-1, 2),
            new Point(-2, 1), new Point(-1, -1), new Point(1, -2));
    private List<Point> all_neighbor_directions = Arrays.asList(
            new Point(1, 0), new Point(1, -1), new Point(0, -1),
            new Point(-1, 0), new Point(-1, 1), new Point(0, 1),
            new Point(2, -1), new Point(1, 1), new Point(-1, 2),
            new Point(-2, 1), new Point(-1, -1), new Point(1, -2));


    Point checkMoves(HexGameStatus tauler, Set<Point> l, int color){
        List<Point> winL =  l.stream().parallel().filter(point -> {
            HexGameStatus nouTauler = new HexGameStatus(tauler);
            nouTauler.placeStone(point, color);
            return nouTauler.isGameOver();
        }).collect(Collectors.toList());

        System.out.println("JAJAJAJA");
        return winL.isEmpty() ? null : winL.get(0);
    }

    Set<Point> getColorPoints(HexGameStatus s, int color) {
        Set<Point> moves = new HashSet<>();
        Stream.iterate(0, n -> n + 1).limit(s.getSize())
                .forEach(i ->
                        moves.addAll(Stream.iterate(0, t -> t + 1).limit(s.getSize()).parallel()
                                .filter(j -> s.getPos(i, j) == color)
                                .map(j -> new Point(i, j))
                                .collect(Collectors.toList())));
        return moves;
    }

    Set<Point> getNonColorPoints(HexGameStatus s, int color) {
        Set<Point> moves = new HashSet<>();
        Stream.iterate(0, n -> n + 1).limit(s.getSize())
                .forEach(i ->
                        moves.addAll(Stream.iterate(0, t -> t + 1).limit(s.getSize()).parallel()
                                .filter(j -> s.getPos(i, j) != color)
                                .map(j -> new Point(i, j))
                                .collect(Collectors.toList())));
        return moves;
    }

    float pointDistance(Point a, Point b) {
        return (Math.abs(a.x - b.x) + Math.abs(a.x + a.y - b.x - b.y) + Math.abs(a.y - b.y)) / 2f;
    }

    Point sumPoint(Point p, Point dir) {
        return new Point(p.x+dir.x, p.y+dir.y);
    }

    List<Point> getNeighbor(Point point){
        return getPoints(point, neighbor_directions);
    }
    List<Point> getDiagonalNeighbor(Point point){
        return getPoints(point, disgonal_neighbor_directions);
    }
    List<Point> getAllNeighbor(Point point){
        return getPoints(point, all_neighbor_directions);
    }

    List<Point> getAllColorNeighbor(HexGameStatus s, Point point, int color){
        return all_neighbor_directions.parallelStream()
                .map(direction -> sumPoint(point,direction))
                .filter(p -> p.x>=0 && p.y>=0 && p.x < s.getSize() && p.y <s.getSize() && s.getPos(p.x, p.y) == color)
                .collect(Collectors.toList());
    }
    List<Point> getAllNonColorNeighbor(HexGameStatus s, Point point, int color){
        return  all_neighbor_directions.parallelStream()
                .map(direction -> sumPoint(point,direction))
                .filter(p -> p.x>=0 && p.y>=0 && p.x < 11 && p.y <11 && s.getPos(p.x, p.y) != color)
                .collect(Collectors.toList());
    }

    private List<Point> getPoints(Point point, List<Point> directions) {
        return directions.parallelStream()
                .map(direction -> sumPoint(point,direction))
                .filter(p -> p.x>=0 && p.y>=0 && p.x < 11 && p.y <11)
                .collect(Collectors.toList());
    }






    /**draw line**/
    Point3D axial_to_cube(Point p){
    int x = p.x;
    int z = p.y;
    int y = -x-z;
    return new Point3D(x, y, z);
    }
    Point cube_to_axial(Point3D cube) {
        int q = (int) cube.getX();
        int r = (int) cube.getZ();
        return new Point(q, r);
    }

    Point3D cube_round(Point3D cube) {
        int rx = (int) cube.getX();
        int ry = (int) cube.getY();
        int rz = (int) cube.getZ();

        int x_diff = (int) Math.abs(rx - cube.getX());
        int y_diff = (int) Math.abs(ry - cube.getY());
        int z_diff = (int) Math.abs(ry - cube.getZ());

        if (x_diff > y_diff && x_diff >z_diff) {
            rx = -ry - rz;
        }else if (y_diff > z_diff) {
            ry = -rx - rz;
        }else{
            rz = -rx - ry;
        }
        return new Point3D(rx, ry, rz);
    }

    Point hex_round(Point p) {
        return cube_to_axial(cube_round(axial_to_cube(p)));
    }

    double lerp(double a, double b, double t) {
        return (a +(b - a) * t);
    }

    Point3D cube_lerp(Point3D a, Point3D b, double t){
        return new Point3D(lerp(a.getX(), b.getX(), t), lerp(a.getY(), b.getY(), t), lerp(a.getZ(), b.getZ(), t));
    }


    List<Point> cube_linedraw(Point a, Point b){
        float N = pointDistance(a, b);
        List<Point> results = new ArrayList<>();
        for ( int i =0; i <= N ; i++){
            results.add(cube_to_axial(cube_round(cube_lerp(axial_to_cube(a), axial_to_cube(b), 1.0 / N * i))));
        }
        return results;
    }








    /**       Fucking shit GRAPH**/




    public Graph initializeGreph(HexGameStatus s, int color){
        Graph graph = new Graph();
        List<Node> nodes = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(s.getSize())
                .forEach(i ->
                        nodes.addAll(Stream.iterate(0, t -> t + 1).limit(s.getSize())
                                .filter(j -> s.getPos(i, j) != -color)
                                .map(j -> new Node(new Point(i, j)))
                                .collect(Collectors.toList())));

        Node D = new Node(new Point(s.getSize(),0));
        Node R = new Node(new Point(0,s.getSize()));
        nodes.stream()
                .filter(n -> n.getPoint().x == s.getSize()-1 || n.getPoint().y == s.getSize()-1)
                .forEach(n -> {
                    if(n.getPoint().y == s.getSize()-1){ n.addDestination(D, 0); }
                    if(n.getPoint().x == s.getSize()-1){ n.addDestination(R, 0); }

                });


        nodes.forEach(node -> {
            neighbor_directions.stream()
                    .map(direction -> sumPoint(node.getPoint(),direction))
                    .filter(p -> p.x>=0 && p.y>=0 && p.x < s.getSize() && p.y <s.getSize() && s.getPos(p.x, p.y) != -color)
                    .forEach(point -> {
                        int distance = s.getPos(point.x, point.y) == 0 ? 2 : 0;
                        int pos = nodes.indexOf(new Node(point));
                        node.addDestination(nodes.get(pos), distance);
            });
            graph.addNode(node);
        });

        Node T = new Node(new Point(-1,0));
        Node L = new Node(new Point(0,-1));
        nodes.stream()
                .filter(n -> n.getPoint().x == 0 || n.getPoint().y == 0)
                .forEach(n -> {
                    if(n.getPoint().y == 0){ T.addDestination(n, 0); }
                    if(n.getPoint().x == 0){ L.addDestination(n, 0); }

        });

        graph.addNode(T);
        graph.addNode(D);
        graph.addNode(L);
        graph.addNode(R);

        return graph;
    }

    public Graph calculateShortestPathFromSource(Graph graph, Node source) {
        try{
            source.setDistance(0);

            Set<Node> settledNodes = new HashSet<>();
            Set<Node> unsettledNodes = new HashSet<>();

            unsettledNodes.add(source);

            while (unsettledNodes.size() != 0) {
                Node currentNode = getLowestDistanceNode(unsettledNodes);
                unsettledNodes.remove(currentNode);
                currentNode.getAdjacentNodes().entrySet().forEach(adjacencyPair -> {
                    Node adjacentNode = adjacencyPair.getKey();
                    Integer edgeWeight = adjacencyPair.getValue();
                    if (!settledNodes.contains(adjacentNode)) {
                        CalculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                        unsettledNodes.add(adjacentNode);
                    }
                });
                settledNodes.add(currentNode);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return graph;
    }


    private Node getLowestDistanceNode(Set < Node > unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private void CalculateMinimumDistance(Node evaluationNode,
                                                 Integer edgeWeigh, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }


}
