package com.ToniC.players;

import com.ToniC.players.Dijkstra.Graph;
import com.ToniC.players.Dijkstra.Node;
import edu.upc.epsevg.prop.hex.HexGameStatus;

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

        return winL.isEmpty() ? null : winL.get(0);
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

    private Point sumPoint(Point p, Point dir) {
        return new Point(p.x+dir.x, p.y+dir.y);
    }

    List<Point> getAllColorNeighbor(HexGameStatus s, Point point, int color){
        return all_neighbor_directions.parallelStream()
                .map(direction -> sumPoint(point,direction))
                .filter(p -> p.x>=0 && p.y>=0 && p.x < s.getSize() && p.y <s.getSize() && s.getPos(p.x, p.y) == color)
                .collect(Collectors.toList());
    }

    List<Point> getEmptyNeighbor(HexGameStatus s, Point point) {
        return neighbor_directions.parallelStream()
                .map(direction -> sumPoint(point,direction))
                .filter(p -> p.x>=0 && p.y>=0 && p.x < 11 && p.y <11 && s.getPos(p.x, p.y) == 0)
                .collect(Collectors.toList());
    }




    /**       Fucking shit GRAPH        **/

    private List<Point> down_neighbor_directions = Arrays.asList(
            new Point(1, 0), new Point(-1, 1), new Point(0, 1), new Point(-1, 0));
    private List<Point> rigth_neighbor_directions = Arrays.asList(
            new Point(0, -1), new Point(1, -1), new Point(1, 0), new Point(0, 1));

    Graph initializeGreph(HexGameStatus s, int color){
        Graph graph = new Graph();
        List<Node> nodes = getNonColorPoints(s,-color).stream().map(Node::new).collect(Collectors.toList());

        Node D = new Node(new Point(s.getSize(),0));
        Node R = new Node(new Point(0,s.getSize()));
        nodes.stream().parallel().filter(n -> n.getPoint().x == s.getSize()-1 || n.getPoint().y == s.getSize()-1)
                .forEach(n -> {
                    if(n.getPoint().y == s.getSize()-1){ n.addDestination(D, 0); }
                    if(n.getPoint().x == s.getSize()-1){ n.addDestination(R, 0); }

                });
        if(color == 1){
            nodes.forEach(node -> {
                rigth_neighbor_directions.stream()
                        .map(direction -> sumPoint(node.getPoint(),direction))
                        .filter(p -> p.x>=0 && p.y>=0 && p.x < s.getSize() && p.y <s.getSize() && s.getPos(p.x, p.y) != -color)
                        .forEach(point -> {
                            int distance = s.getPos(point.x, point.y) == 0 ? 1 : 0;
                            int pos = nodes.indexOf(new Node(point));
                            node.addDestination(nodes.get(pos), distance);
                        });
                graph.addNode(node);
            });
        }else if(color == -1){
            nodes.forEach(node -> {
                down_neighbor_directions.stream()
                        .map(direction -> sumPoint(node.getPoint(),direction))
                        .filter(p -> p.x>=0 && p.y>=0 && p.x < s.getSize() && p.y <s.getSize() && s.getPos(p.x, p.y) != -color)
                        .forEach(point -> {
                            int distance = s.getPos(point.x, point.y) == 0 ? 1 : 0;
                            int pos = nodes.indexOf(new Node(point));
                            node.addDestination(nodes.get(pos), distance);
                        });
                graph.addNode(node);
            });
        }

        Node T = new Node(new Point(-1,0));
        Node L = new Node(new Point(0,-1));
        nodes.stream().parallel().filter(n -> n.getPoint().x == 0 || n.getPoint().y == 0)
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

    List<Node> CalculateShortestPath(Graph g ,int color){
        int startPos;
        int endPos;
        if(color == 1) {
            startPos =g.getNodes().size() - 2;
            endPos =g.getNodes().size() - 1;
        }else {
            startPos =g.getNodes().size() - 4;
            endPos =g.getNodes().size() - 3;
        }

        calculateShortestPathFromSource(g, g.getNodes().get(startPos), g.getNodes().get(endPos));

        return g.getNodes().get(endPos).getShortestPath();
    }

    private Graph calculateShortestPathFromSource(Graph graph, Node source,  Node end) {
        try{
            source.setDistance(0);

            Set<Node> settledNodes = new HashSet<>();
            Set<Node> unsettledNodes = new HashSet<>();

            unsettledNodes.add(source);

            while (!unsettledNodes.contains(end) && unsettledNodes.size() != 0) {
                Node currentNode = getLowestDistanceNode(unsettledNodes);
                unsettledNodes.remove(currentNode);
                currentNode.getAdjacentNodes().forEach((adjacentNode, edgeWeight) -> {
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

    private void CalculateMinimumDistance(Node evaluationNode, Integer edgeWeigh, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
