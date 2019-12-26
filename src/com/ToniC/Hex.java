package com.ToniC;

import com.ToniC.players.*;
import edu.upc.epsevg.prop.hex.*;
import edu.upc.epsevg.prop.hex.players.ProfePlayer;

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

                IPlayer parallel = new ParallelPlayer(1);
                IPlayer parallel1 = new ParallelPlayer(2);
                IPlayer test = new TesterClass();
                IPlayer pp = new ProfePlayer("fasd",2);


                int n = 11;
                IPlayer human = new HumanPlayer("Paco");
                new HexBoard(new HexGameStatus(n), pp,parallel);

            }
        });
    }
}
