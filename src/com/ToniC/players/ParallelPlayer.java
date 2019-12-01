package com.ToniC.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ParallelPlayer implements IPlayer, IAuto {


    private int depth, color, bestAlpha, winDepth, loseDepth;

    private boolean heuristic;

    public ParallelPlayer(int depth, boolean heuristic) {
        this.depth = depth;
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "Parallel";
    }

    @Override
    public Point move(HexGameStatus tauler, int c) {
        color = c;

        AtomicReference<Point> bestmove = new AtomicReference<>(new Point(-1, -1));

        bestAlpha = Integer.MIN_VALUE;

        List<Point> l = getMoviments(tauler);
        System.out.println(l);
        Point p = checkMoves(tauler, l);
        if (p != null) return p;


//        l.parallelStream().parallel().forEach(moviment -> {
//            HexGameStatus nouTauler = new HexGameStatus(tauler);
//            nouTauler.placeStone(moviment, color);
//
//            List<Point> newList = new ArrayList<>(l);
//            newList.remove(moviment);
//            int aux = min_value(nouTauler, newList, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
//
//            System.out.println(aux+"    "+moviment+"     "+bestAlpha);
//
//            if (aux > bestAlpha) {
//                bestmove.set(moviment);
//                bestAlpha = aux;
//
//            }
//        });


        Parallel.For(l, moviment -> {
            HexGameStatus nouTauler = new HexGameStatus(tauler);
            nouTauler.placeStone(moviment, color);

            List<Point> newList = new ArrayList<>(l);
            newList.remove(moviment);
            int aux = min_value(nouTauler, newList, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);

            System.out.println(aux+"    "+moviment+"     "+bestAlpha);

            if (aux >= bestAlpha) {
                bestmove.set(moviment);
                bestAlpha = aux;

            }
        });


        return bestmove.get();
    }

    private Point checkMoves(HexGameStatus tauler, List<Point> l){
        List<Point> winL =  l.stream().parallel().filter(point -> {
            HexGameStatus nouTauler = new HexGameStatus(tauler);
            nouTauler.placeStone(point, color);
            return nouTauler.isGameOver();
        }).collect(Collectors.toList());

        return winL.isEmpty() ? null : winL.get(0);
    }

    private java.util.List<Point> getMoviments(HexGameStatus s) {
        List<Point> moves = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(s.getSize())
                .forEach(i ->
                        moves.addAll(Stream.iterate(0, t -> t + 1).limit(s.getSize()).parallel()
                                .filter(j -> s.getPos(j, i) == 0)
                                .map(j -> new Point(j, i))
                                .collect(Collectors.toList())));
        return moves;
    }

    /**
     *
     * @param t Classe Tauler.
     * @param alpha Enter Alpha.
     * @param beta Enter Beta.
     * @param d Enter profunditat.
     * @return Enter.
     */
    private int max_value(HexGameStatus t, List<Point> l, int alpha, int beta, int d){
        if(d==0) {
            return 0;
        }
        else{
            for(Point moviment : l) {
                HexGameStatus nouTauler = new HexGameStatus(t);
                nouTauler.placeStone(moviment,color);

                if(nouTauler.isGameOver()){ winDepth = d; return Integer.MAX_VALUE; }

                List<Point> newList = new ArrayList<>(l);
                newList.remove(moviment);
                alpha = Math.max(alpha, min_value(nouTauler, newList, alpha, beta, d));

                if(beta <= alpha) return beta;
            }
            return alpha;
        }
    }

    private int min_value(HexGameStatus t, List<Point> l, int alpha, int beta, int d) {
        for(Point moviment : l){
            HexGameStatus nouTauler = new HexGameStatus(t);
            nouTauler.placeStone(moviment,-color);

            if(nouTauler.isGameOver()){ loseDepth = d; return Integer.MIN_VALUE; }

            List<Point> newList = new ArrayList<>(l);
            newList.remove(moviment);
            beta = Math.min(beta, max_value(nouTauler, newList, alpha, beta, d-1));

            if(beta < bestAlpha) return Integer.MIN_VALUE;
            if(beta <= alpha) return alpha;
        }
        return beta;
    }
    

}
