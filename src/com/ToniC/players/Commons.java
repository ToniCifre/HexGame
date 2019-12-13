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
            new Point(1, 0), new Point(0, 1), new Point(-1, 1), new Point(-1, 0));
    private List<Point> rigth_neighbor_directions = Arrays.asList(
            new Point(0, -1), new Point(1, -1), new Point(1, 0), new Point(0, 1));

    private List<Point> rigth_neighbor_closse_directions = Arrays.asList(
             new Point(0, 1), new Point(0, -1));
    private List<Point> down_neighbor_closse_directions = Arrays.asList(
            new Point(1, 0), new Point(-1, 1));

    Graph initializeGraph(HexGameStatus s, int color){
        Graph graph = new Graph();
        List<Node> nodes = getNonColorPoints(s,-color).stream().map(Node::new).collect(Collectors.toList());

        if(color == 1){
            Node end = new Node(new Point(s.getSize(), 0));
            nodes.stream().parallel().filter(n -> n.getPoint().x == s.getSize()-1)
                    .forEach(n -> n.addDestination(end, 0));
            graph.addNode(end);

            nose(s, color, graph, nodes, rigth_neighbor_directions, rigth_neighbor_closse_directions);

            Node start = new Node(new Point(-1,0));
            nodes.stream().parallel().filter(n -> n.getPoint().x == 0)
                    .forEach(n -> {
                            if (s.getPos(n.getPoint().x, n.getPoint().y) == color) start.addDestination(n, 0);
                            else start.addDestination(n, 1);
                    });
            graph.addNode(start);

        }else if(color == -1){
            Node end = new Node(new Point(0,s.getSize()));
            nodes.stream().parallel().filter(n -> n.getPoint().y == s.getSize()-1)
                    .forEach(n ->  n.addDestination(end, 0));
            graph.addNode(end);

            nose(s, color, graph, nodes, down_neighbor_directions, down_neighbor_closse_directions);

            Node start = new Node(new Point(0,-1));
            nodes.stream().parallel().filter(n -> n.getPoint().y == 0)
                    .forEach(n -> {
                            if (s.getPos(n.getPoint().x, n.getPoint().y) == color) start.addDestination(n, 0);
                            else start.addDestination(n, 1);
                    });
            graph.addNode(start);
        }

        return graph;
    }

    private void nose(HexGameStatus s, int color, Graph graph, List<Node> nodes, List<Point> neighbor_directions,
                      List<Point> closs_positions) {

        nodes.forEach(node -> {
            neighbor_directions.stream()
                    .map(direction -> sumPoint(node.getPoint(),direction))
                    .filter(p -> p.x>=0 && p.y>=0 && p.x <s.getSize() && p.y<s.getSize() && s.getPos(p.x, p.y) != -color)
                    .forEach(point -> {

                        closs_positions.stream().map(direction -> sumPoint(point,direction))
                                .filter(p -> p.x>=0 && p.y>=0 && p.x <s.getSize() && p.y<s.getSize() && s.getPos(p.x, p.y) == -color)
                                .count();


                        int distance = s.getPos(point.x, point.y) == 0 ? 1 : 0;
                        int pos = nodes.indexOf(new Node(point));
                        node.addDestination(nodes.get(pos), distance);
                    });
            graph.addNode(node);
        });
    }

    List<Node> CalculateShortestPath(Graph g){

        calculateShortestPath(g, g.getNodes().get(g.getNodes().size()-1), g.getNodes().get(0));

        return g.getNodes().get(0).getShortestPath();
    }

    private Graph calculateShortestPath(Graph graph, Node source,  Node end) {
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



    float euristic(HexGameStatus tauler, int color){
        try {
            float score;
            float scoreEnemy;

            Graph g = initializeGraph(tauler,color);
            Graph gEnemy = initializeGraph(tauler,-color);

            score = getScoreFromPath(CalculateShortestPath(g), tauler, color);
            scoreEnemy = getScoreFromPath(CalculateShortestPath(gEnemy), tauler, -color);

            System.out.println(score +"     -    "+scoreEnemy+"      --     "+(score - (scoreEnemy)));
            return score - (scoreEnemy);
        }catch (Exception e){
            e.printStackTrace();
            return -999999999;
        }
    }


    float getScoreFromPath(List<Node> shortestPath, HexGameStatus s, int color){
        if(!shortestPath.isEmpty()){
            /*float score = 0;
            List<Node> ll = shortestPath.subList(1,shortestPath.size());
            for (int i = 0, llSize = ll.size()-2; i < llSize; i++) {
                Node n = ll.get(i);
                Node n2 = ll.get(i+2);
                if(     s.getPos(n.getPoint().x,n.getPoint().y) == color &&
                        s.getPos(n2.getPoint().x,n2.getPoint().y) == color &&
                        s.getPos(ll.get(i+1).getPoint().x,ll.get(i+1).getPoint().y) == 0)
                {

                    long num = getEmptyNeighbor(s, n.getPoint()).stream()
                            .filter(point -> getEmptyNeighbor(s, n2.getPoint()).contains(point) ).count();
                    if (num==2){
                        score+=0.5;
                    }
                }
            }*/
            int distance = shortestPath.get(shortestPath.size()-1).getDistance();
            if(distance == 0)return -999999999;
            return 21-distance;
        }
        System.out.println("no cami");
        return -99999999;
    }




    void print_graph(Graph g){
        g.getNodes().forEach(node -> {
            System.out.print(node.getPoint());
            node.getAdjacentNodes().forEach((node1, integer) -> System.out.print(integer+" - "));
            System.out.println();
        });
    }
}
