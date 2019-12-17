package com.ToniC.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;

import java.awt.*;

public class TestPlayer implements IPlayer, IAuto {

    private String name;
    private int i =0, j = 0;

    public TestPlayer(String name) {
        this.name = name;
    }

    @Override
    public Point move(HexGameStatus s, int color) {
        for (i = 0; i < s.getSize(); i++) {
            for (j = 0; j < s.getSize()-1; j++) {
                if (s.getPos(i, j) == 0) {
                    return new Point(i, j);
                }else{
                }
            }
        }
        return new Point(0,0);
    }


    @Override
    public String getName() {
        return name;
    }

}
