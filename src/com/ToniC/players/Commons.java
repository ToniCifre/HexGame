package com.ToniC.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import javafx.geometry.Point3D;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Commons {

    private List<Point> neighbor_directions = Arrays.asList(
            new Point(1, 0), new Point(1, -1), new Point(0, -1),
            new Point(-1, 0), new Point(-1, 1), new Point(0, 1));
    private List<Point> disgonal_neighbor_directions = Arrays.asList(
            new Point(2, -1), new Point(1, 1), new Point(-1, 2),
            new Point(-2, 1), new Point(-1, -1), new Point(1, -2));
    private List<Point> all_neighbor_directions = Arrays.asList(
            new Point(1, 0), new Point(1, -1), new Point(0, -1),
            new Point(-1, 0), new Point(-1, 1), new Point(0, 1),
            new Point(2, -1), new Point(1, 1), new Point(-1, 2),
            new Point(-2, 1), new Point(-1, -1), new Point(1, -2));


    Point checkMoves(HexGameStatus tauler, Set<Point> l, int color){
        List<Point> winL =  l.stream().parallel().filter(point -> {
            HexGameStatus nouTauler = new HexGameStatus(tauler);
            nouTauler.placeStone(point, color);
            return nouTauler.isGameOver();
        }).collect(Collectors.toList());

        return winL.isEmpty() ? null : winL.get(0);
    }

    Set<Point> getColorPoints(HexGameStatus s, int color) {
        Set<Point> moves = new HashSet<>();
        Stream.iterate(0, n -> n + 1).limit(s.getSize())
                .forEach(i ->
                        moves.addAll(Stream.iterate(0, t -> t + 1).limit(s.getSize()).parallel()
                                .filter(j -> s.getPos(i, j) == color)
                                .map(j -> new Point(i, j))
                                .collect(Collectors.toList())));
        return moves;
    }

    Set<Point> getNonColorPoints(HexGameStatus s, int color) {
        Set<Point> moves = new HashSet<>();
        Stream.iterate(0, n -> n + 1).limit(s.getSize())
                .forEach(i ->
                        moves.addAll(Stream.iterate(0, t -> t + 1).limit(s.getSize()).parallel()
                                .filter(j -> s.getPos(i, j) != color)
                                .map(j -> new Point(i, j))
                                .collect(Collectors.toList())));
        return moves;
    }

    float pointDistance(Point a, Point b) {
        return (Math.abs(a.x - b.x) + Math.abs(a.x + a.y - b.x - b.y) + Math.abs(a.y - b.y)) / 2f;
    }

    Point sumPoint(Point p, Point dir) {
        return new Point(p.x+dir.x, p.y+dir.y);
    }

    List<Point> getNeighbor(Point point){
        return getPoints(point, neighbor_directions);
    }
    List<Point> getDiagonalNeighbor(Point point){
        return getPoints(point, disgonal_neighbor_directions);
    }
    List<Point> getAllNeighbor(Point point){
        return getPoints(point, all_neighbor_directions);
    }

    List<Point> getAllColorNeighbor(HexGameStatus s, Point point, int color){
        return all_neighbor_directions.parallelStream()
                .map(direction -> sumPoint(point,direction))
                .filter(p -> p.x>=0 && p.y>=0 && p.x < 11 && p.y <11 && s.getPos(p.x, p.y) == color)
                .collect(Collectors.toList());
    }
    List<Point> getAllNonColorNeighbor(HexGameStatus s, Point point, int color){
        return  all_neighbor_directions.parallelStream()
                .map(direction -> sumPoint(point,direction))
                .filter(p -> p.x>=0 && p.y>=0 && p.x < 11 && p.y <11 && s.getPos(p.x, p.y) != color)
                .collect(Collectors.toList());
    }

    private List<Point> getPoints(Point point, List<Point> directions) {
        return directions.parallelStream()
                .map(direction -> sumPoint(point,direction))
                .filter(p -> p.x>=0 && p.y>=0 && p.x < 11 && p.y <11)
                .collect(Collectors.toList());
    }






    /**draw line**/
    Point3D axial_to_cube(Point p){
    int x = p.x;
    int z = p.y;
    int y = -x-z;
    return new Point3D(x, y, z);
    }
    Point cube_to_axial(Point3D cube) {
        int q = (int) cube.getX();
        int r = (int) cube.getZ();
        return new Point(q, r);
    }

    Point3D cube_round(Point3D cube) {
        int rx = (int) cube.getX();
        int ry = (int) cube.getY();
        int rz = (int) cube.getZ();

        int x_diff = (int) Math.abs(rx - cube.getX());
        int y_diff = (int) Math.abs(ry - cube.getY());
        int z_diff = (int) Math.abs(ry - cube.getZ());

        if (x_diff > y_diff && x_diff >z_diff) {
            rx = -ry - rz;
        }else if (y_diff > z_diff) {
            ry = -rx - rz;
        }else{
            rz = -rx - ry;
        }
        return new Point3D(rx, ry, rz);
    }

    Point hex_round(Point p) {
        return cube_to_axial(cube_round(axial_to_cube(p)));
    }

    double lerp(double a, double b, double t) {
        return (a +(b - a) * t);
    }

    Point3D cube_lerp(Point3D a, Point3D b, double t){
        return new Point3D(lerp(a.getX(), b.getX(), t), lerp(a.getY(), b.getY(), t), lerp(a.getZ(), b.getZ(), t));
    }


    List<Point> cube_linedraw(Point a, Point b){
        float N = pointDistance(a, b);
        List<Point> results = new ArrayList<>();
        for ( int i =0; i <= N ; i++){
            results.add(cube_to_axial(cube_round(cube_lerp(axial_to_cube(a), axial_to_cube(b), 1.0 / N * i))));
        }
        return results;
    }
}
