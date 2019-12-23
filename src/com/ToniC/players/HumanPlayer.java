/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ToniC.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IPlayer;
import java.awt.Point;

/**
 *
 * @author bernat
 */
public class HumanPlayer  implements IPlayer{
       String name;
    
    public HumanPlayer(String name) {
        this.name = name;
    }

    
    @Override
    public Point move(HexGameStatus s, int color) {
        return null;
    }
    
 

    @Override
    public String getName() {
        return "Human("+name+")";
    } 
}
