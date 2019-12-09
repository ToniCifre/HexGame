package com.ToniC.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


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

        Set<Point> allStones = commons.getNonColorPoints(tauler, 0);
        Set<Point> l;
        if(!allStones.isEmpty()){
            l = new HashSet<>();
            allStones.stream().parallel()
                    .map(point -> commons.getAllColorNeighbor(tauler, point,0))
                    .forEach(l::addAll);

            System.out.println(l);
        }else{
            return new Point(5,5);
        }

//        Point p = commons.checkMoves(tauler, l, color);
//        if (p != null) return p;

        bestAlpha = new AtomicReference<>(Float.NEGATIVE_INFINITY);
        AtomicReference<Point> bestmove = new AtomicReference<>(new Point(-1, -1));
        Parallel.For(l, moviment -> {
            HexGameStatus nouTauler = new HexGameStatus(tauler);
            nouTauler.placeStone(moviment, color);

            Set<Point> newList = new HashSet<>(l);
            newList.remove(moviment);
            newList.addAll(commons.getAllColorNeighbor(tauler, moviment, 0));
            float aux = min_value(nouTauler, newList, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, depth);

            System.out.println(aux+"    "+moviment+"     "+bestAlpha);

            if (aux >= bestAlpha.get()) {
                bestmove.set(moviment);
                bestAlpha.set(aux);
            }
        });


        return bestmove.get();
    }


    private float max_value(HexGameStatus t, Set<Point> l, float alpha, float beta, int d){
        if(d<=0) { return 0; }
        else{
            for(Point moviment : l) {
                HexGameStatus nouTauler = new HexGameStatus(t);
                nouTauler.placeStone(moviment,color);

                if(nouTauler.isGameOver()){ return Float.POSITIVE_INFINITY; }

                Set<Point> newList = new HashSet<>(l);
                newList.remove(moviment);
                newList.addAll(commons.getAllColorNeighbor(t, moviment, 0));
                alpha = Math.max(alpha, min_value(nouTauler, newList, alpha, beta, d));

                if(beta <= alpha) return beta;
            }
            return alpha;
        }
    }

    private float min_value(HexGameStatus t, Set<Point> l, float alpha, float beta, int d) {
        for(Point moviment : l){
            HexGameStatus nouTauler = new HexGameStatus(t);
            nouTauler.placeStone(moviment,-color);

            if(nouTauler.isGameOver()){ return Float.NEGATIVE_INFINITY; }

            Set<Point> newList = new HashSet<>(l);
            newList.remove(moviment);
            newList.addAll(commons.getAllColorNeighbor(t, moviment, 0));
            beta = Math.min(beta, max_value(nouTauler, newList, alpha, beta, d-1));

            if(beta < bestAlpha.get()) return alpha;
            if(beta <= alpha) return alpha;
        }
        return beta;
    }


}
