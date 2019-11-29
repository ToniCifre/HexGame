package com.ToniC;

import com.ToniC.players.HumanPlayer;
import com.ToniC.players.PenetratorPlayer;
import com.ToniC.players.RandomPlayer;
import com.ToniC.players.TestPlayer;
import edu.upc.epsevg.prop.hex.*;

import javax.swing.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.


import edu.upc.epsevg.prop.hex.players.*;

/**
 *
 * @author bernat
 */
public class Hex {


    public static void main(String[] args) {
        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                IPlayer test = new TestPlayer("test");
                IPlayer toni = new PenetratorPlayer(2, true);


                int n = 11;
                IPlayer random = new RandomPlayer("Crazy Ivan");
                IPlayer human = new HumanPlayer("Paco");
                new HexBoard(new HexGameStatus(n), toni, human);
                
            }
        });
    }
}
