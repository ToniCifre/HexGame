package com.ToniC.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
        Point bestmove = new Point(-1,-1);
        int minProf = 0, maxProf=depth+1;

        bestAlpha = Integer.MIN_VALUE;

        List<Point> l = getMoviments(s);
        for(Point moviment : l) {
            winDepth = 0;
            loseDepth = depth;

            HexGameStatus nouTauler = new HexGameStatus(s);
            nouTauler.placeStone(moviment, color);

            int aux = min_value(nouTauler, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);

            System.out.println("alpha:"+aux+"   |   move:"+moviment.toString());

            if (aux >= bestAlpha) {
                bestmove = moviment;
                bestAlpha = aux;
            }
        }
        return bestmove;
    }

    private List<Point> getMoviments(HexGameStatus s) {
        List<Point> moves = new ArrayList<>();
        for (int i = 0; i < s.getSize(); i++) {
            for (int j = 0; j < s.getSize(); j++) {
                if (s.getPos(j, i) == 0) {
                    moves.add(new Point(j, i));
                }
            }
        }
        return moves;
    }

    private int max_value(HexGameStatus t, int alpha, int beta, int d){
        if(d==0) {
            return 0;//heuristic(t);
        }
        else{
            for(Point moviment : getMoviments(t)) {
                HexGameStatus nouTauler = new HexGameStatus(t);
                nouTauler.placeStone(moviment,color);

                if(nouTauler.isGameOver()){
                    winDepth = d;
                    return Integer.MAX_VALUE;
                }

                alpha = Math.max(alpha, min_value(nouTauler, alpha, beta, d));
                if(beta <= alpha) return beta;
            }
            return alpha;
        }
    }

    private int min_value(HexGameStatus t, int alpha, int beta, int d) {
        for(Point moviment : getMoviments(t)){
            HexGameStatus nouTauler = new HexGameStatus(t);
            nouTauler.placeStone(moviment, -color);

            if(nouTauler.isGameOver()){
                loseDepth = d;
                return Integer.MIN_VALUE;
            }

            beta = Math.min(beta, max_value(nouTauler, alpha, beta, d-1));

            if(beta < bestAlpha) return Integer.MIN_VALUE;
            if(beta <= alpha) return alpha; 
        }
        return beta;
    }

}