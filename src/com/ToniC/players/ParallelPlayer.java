package com.ToniC.players;

import com.ToniC.players.Dijkstra.Graph;
import com.ToniC.players.Dijkstra.Node;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class ParallelPlayer implements IPlayer, IAuto {

    private Commons commons = new Commons();

    private int depth, color;
    private AtomicReference<Float> bestAlpha;
    private boolean heuristic;

    public ParallelPlayer(int depth, boolean heuristic) {
        this.depth = depth;
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "Penetrator";
    }

    @Override
    public Point move(HexGameStatus tauler, int color) {
        this.color = color;

        Set<Point> moves;
        Set<Point> allStones = commons.getNonColorPoints(tauler, 0);
        if(!allStones.isEmpty()){
            moves = new HashSet<>();
            allStones.stream().parallel()
                    .map(point -> commons.getAllColorNeighbor(tauler, point,0))
                    .forEach(moves::addAll);
        }else{
            return new Point(5,5);
        }

        Point p = commons.checkMoves(tauler, moves, color);
        if (p != null) return p;

        bestAlpha = new AtomicReference<>(Float.NEGATIVE_INFINITY);
        AtomicReference<Point> bestmove = new AtomicReference<>(new Point(-1, -1));
        Parallel.For(moves, moviment -> {
            HexGameStatus nouTauler = new HexGameStatus(tauler);
            nouTauler.placeStone(moviment, color);

            Set<Point> newList = new HashSet<>(moves);
            newList.remove(moviment);
            newList.addAll(commons.getAllColorNeighbor(tauler, moviment, 0));
            float aux = min_value(nouTauler, newList, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, depth-1);

            System.out.println(aux+"    "+moviment+"     "+bestAlpha);

            if (aux > bestAlpha.get()) {
                bestmove.set(moviment);
                bestAlpha.set(aux);
            }
        });

        System.out.println("Moiment final --> "+bestmove.get());
        if(bestmove.get().equals(new Point(-1, -1)))return moves.iterator().next();
        return bestmove.get();
    }


    private float max_value(HexGameStatus t, Set<Point> moves, float alpha, float beta, int d){
        if(d<=0) { return euristic(t);
        } else{
            for(Point moviment : moves) {
                HexGameStatus nouTauler = new HexGameStatus(t);
                nouTauler.placeStone(moviment,color);

                if(nouTauler.isGameOver()){ return Float.POSITIVE_INFINITY; }

                Set<Point> newList = new HashSet<>(moves);
                newList.remove(moviment);
                newList.addAll(commons.getAllColorNeighbor(t, moviment, 0));
                alpha = Math.max(alpha, min_value(nouTauler, newList, alpha, beta, d-1));

                if(beta <= alpha) return beta;
            }
            return alpha;
        }
    }

    private float min_value(HexGameStatus t, Set<Point> moves, float alpha, float beta, int d) {
        if(d<=0) { return euristic(t);
        } else {
            for (Point moviment : moves) {
                HexGameStatus nouTauler = new HexGameStatus(t);
                nouTauler.placeStone(moviment, -color);

                if (nouTauler.isGameOver()) {
                    return Float.NEGATIVE_INFINITY;
                }

                Set<Point> newList = new HashSet<>(moves);
                newList.remove(moviment);
                newList.addAll(commons.getAllColorNeighbor(t, moviment, 0));
                beta = Math.min(beta, max_value(nouTauler, newList, alpha, beta, d-1));

                if (beta < bestAlpha.get()) return alpha;
                if (beta <= alpha) return alpha;
            }
            return beta;
        }
    }



    float getScoreFromPath(List<Node> shortestPath, HexGameStatus s, int color){
        if(!shortestPath.isEmpty()){
            float score = 0;
            /*List<Node> ll = shortestPath.subList(1,shortestPath.size());
            for (int i = 0, llSize = ll.size()-2; i < llSize; i++) {
                Node n = ll.get(i);
                Node n2 = ll.get(i+2);
                if(s.getPos(n.getPoint().x,n.getPoint().y)==color
                        && s.getPos(n2.getPoint().x,n2.getPoint().y)==color
                        && s.getPos(ll.get(i+1).getPoint().x,n.getPoint().y)==0){
                    long num = commons.getEmptyNeighbor(s, n.getPoint()).stream()
                            .filter(point -> commons.getEmptyNeighbor(s, n2.getPoint()).contains(point) ).count();
                    if (num==2){
                        score+=0.5;
                    }
                }
            }*/
            int distance = shortestPath.get(shortestPath.size()-1).getDistance();
            if(distance == 0)return Float.NEGATIVE_INFINITY;
            return 21/Math.max(0.0f, distance - score);

        }
        return Float.NEGATIVE_INFINITY;
    }

    float euristic(HexGameStatus tauler){
        try {
            Graph g = commons.initializeGreph(tauler,color);
            Graph gEnemy = commons.initializeGreph(tauler,-color);
            float score;
            float scoreEnemy;

            score = getScoreFromPath(commons.CalculateShortestPath(g, color), tauler, color);
            scoreEnemy = getScoreFromPath(commons.CalculateShortestPath(gEnemy, -color), tauler, -color);

            return score - scoreEnemy;
        }catch (Exception e){
            e.printStackTrace();
            return -999999999;
        }
    }
}
