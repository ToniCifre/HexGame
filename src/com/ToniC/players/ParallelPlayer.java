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

    private int torns, color;
    private AtomicReference<Float> bestAlpha;
    private boolean heuristic;

    public ParallelPlayer(int torns, boolean heuristic) {
        this.torns = torns;
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "Penetrator";
    }

    @Override
    public Point move(HexGameStatus tauler, int color) {
        this.color = color;
        Set<Point> moves;// = commons.getColorPoints(tauler,0);
        Set<Point> allStones = commons.getNonColorPoints(tauler, 0);
        if(!allStones.isEmpty()){
            moves = new HashSet<>();
            allStones.stream().parallel()
                    .map(point -> commons.getAllColorNeighbor(tauler, point,0))
                    .forEach(moves::addAll);
        }else{ return new Point(5,5); }

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
            float aux = min_value(nouTauler, newList, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, torns);

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


    private float max_value(HexGameStatus t, Set<Point> moves, float alpha, float beta, int torn){
        if(torn<=0) { return commons.euristic(t, color);
        } else{
            for(Point moviment : moves) {
                HexGameStatus nouTauler = new HexGameStatus(t);
                nouTauler.placeStone(moviment,color);

                if(nouTauler.isGameOver()){ return Float.POSITIVE_INFINITY; }

                Set<Point> newList = new HashSet<>(moves);
                newList.remove(moviment);
                newList.addAll(commons.getAllColorNeighbor(t, moviment, 0));
                alpha = Math.max(alpha, min_value(nouTauler, newList, alpha, beta, torn));

                if(beta <= alpha) return beta;
            }
            return alpha;
        }
    }

    private float min_value(HexGameStatus t, Set<Point> moves, float alpha, float beta, int torn) {
        for (Point moviment : moves) {
            HexGameStatus nouTauler = new HexGameStatus(t);
            nouTauler.placeStone(moviment, -color);

            if (nouTauler.isGameOver()) {
                return Float.NEGATIVE_INFINITY;
            }

            Set<Point> newList = new HashSet<>(moves);
            newList.remove(moviment);
            newList.addAll(commons.getAllColorNeighbor(t, moviment, 0));
            beta = Math.min(beta, max_value(nouTauler, newList, alpha, beta, torn-1));

            if (beta < bestAlpha.get()) return alpha;
            if (beta <= alpha) return alpha;
        }
        return beta;
    }
}
