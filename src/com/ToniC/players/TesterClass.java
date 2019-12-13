package com.ToniC.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TesterClass implements IPlayer, IAuto {

    public TesterClass() { }

    Commons c = new Commons();
    List<Point> pointList =  Arrays.asList(
            new Point(5, 5), new Point(6, 4), new Point(7, 3),
            new Point(4, 6), new Point(3, 7), new Point(2, 8));

    private int pos = -1;

    @Override
    public Point move(HexGameStatus tauler, int color) {

        c.euristic(tauler, color);

        pos++;
        return pointList.get(pos);


    }








    @Override
    public String getName() {
        return "TesterMan";
    }

}
