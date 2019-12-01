package com.ToniC.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PenetratorPlayer implements IPlayer, IAuto {
    private boolean heuristic;


    private int depth, winDepth, loseDepth;
    private int color;
    private int bestAlpha;

    public PenetratorPlayer(int depth, boolean heuristic) {
        this.depth = depth;
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "Toni";
    }

    @Override
    public Point move(HexGameStatus s, int color) {
        this.color = color;
        bestAlpha = Integer.MIN_VALUE;

        List<Callable<Pair<Point, Integer>>> callables = new ArrayList<>();

        List<Point> l = getMoviments(s);
        for(Point moviment : l) {
            HexGameStatus nouTauler = new HexGameStatus(s);
            nouTauler.placeStone(moviment, color);
            if (nouTauler.isGameOver()) {
                return moviment;
            }

            List<Point> newL = new ArrayList<>(l);
            newL.remove(moviment);
            callables.add(() -> new Pair<>(moviment, min_value(nouTauler, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, newL)));

        }

        AtomicReference<Point> bestmove = new AtomicReference<>(new Point(-1, -1));
        executeTread(callables).forEach(pair -> {
            if ((int)pair.getValue() >= bestAlpha) {
                    bestmove.set((Point) pair.getKey());
                    bestAlpha = (int)pair.getValue();
                }
        });


        System.out.println("move: "+bestmove.toString());
        System.out.println("--------------------------------------------");
        return bestmove.get();
    }


    private <T> List<T> executeTread(List<Callable<T>> callables ){
        ExecutorService executor = Executors.newScheduledThreadPool(11);
        try {
            return executor.invokeAll(callables)
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        }
                        catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    private List<Point> getMoviments(HexGameStatus s) {
        List<Point> moves = new ArrayList<>();
        for (int i = 0; i < s.getSize(); i++) {
            for (int j = 0; j < s.getSize(); j++) {
                if (s.getPos(i,j) == 0) {
                    moves.add(new Point(i, j));
                }
            }
        }
        return moves;
    }

    private int max_value(HexGameStatus t, int alpha, int beta, int d, List<Point> l){
        if(d==0) {
            return 0;
        }
        else{
            for(Point moviment : l) {
                HexGameStatus nouTauler = new HexGameStatus(t);
                nouTauler.placeStone(moviment,color);

                if(nouTauler.isGameOver()){ return Integer.MAX_VALUE; }

                List<Point> newL = new ArrayList<>(l);
                newL.remove(moviment);
                alpha = Math.max(alpha, min_value(nouTauler, alpha, beta, d, newL));

                if(beta <= alpha) return beta;
            }
            return alpha;
        }
    }

    private int min_value(HexGameStatus t, int alpha, int beta, int d, List<Point> l) {

        for(Point moviment : l){
            HexGameStatus nouTauler = new HexGameStatus(t);
            nouTauler.placeStone(moviment, -color);

            if(nouTauler.isGameOver()){ return Integer.MIN_VALUE; }

            List<Point> newL = new ArrayList<>(l);
            newL.remove(moviment);
            beta = Math.min(beta, max_value(nouTauler, alpha, beta, d-1, newL));

            if(beta < bestAlpha) {
                System.out.println("Eliminadfasdfasdjfhalskjdhfklashdkjfhaklsdhflkasd");
                return Integer.MIN_VALUE;}
            if(beta <= alpha) return alpha;

        }
        return beta;
    }



//
//
//    int getHeuristicScore() -> Double {
//        let computerPath = getComputerShortestPath()
//        let playerPath = getPlayerShortestPath()
//        func getScoreForPath(path: Path?, value: HexValue) -> Double {
//            if let path = path as Path! {
//            if path.distance == 0.0 {
//                return -Double.infinity // Game over
//            } else {
//                return Double(path.pathHexes.filter { $0.value == .undefined }.count) // .undefined == gray tiles
//            }
//          }
//            return 0.0
//        }
//
//        let computerScore = getScoreForPath(path: computerPath, value: .computer)
//        let playerScore = getScoreForPath(path: playerPath, value: .player)
//        return playerScore - computerScore
//    }

}