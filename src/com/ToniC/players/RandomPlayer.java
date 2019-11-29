/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ToniC.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author bernat
 */
public class RandomPlayer implements IPlayer, IAuto {

    String name;
    
    public RandomPlayer(String name) {
        this.name = name;
    }

    
    @Override
    public Point move(HexGameStatus s, int color) {

        while(true) {
        Random rand = new Random();
        int i = rand.nextInt(s.getSize()) ;
        int j = rand.nextInt(s.getSize());
            if (s.getPos(i, j) == 0) {
                return new Point(i, j);
            }
        }        
    }
    
 
    @Override
    public String getName() {
        return "Random("+name+")";
    }
    
}
