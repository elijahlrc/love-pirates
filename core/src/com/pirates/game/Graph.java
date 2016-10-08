package com.pirates.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Graph {
    private static final int LEVELS = 50;
    private static final int TRIALS = 10;
    public static void main(String[] args) {
        PrintWriter out;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter("graph.csv")));
        }
        catch (java.io.IOException e) {
            System.out.println("Error!");
            return;
        }

        for (int level = 0; level < LEVELS; ++level) {
            float sum = 0;
            for (int t = 0; t < TRIALS; ++t) {
                Ship ship = LovePirates.basicShipGen.genShip(level,300,300);
                sum += ship.getPower();
            }
            out.println(sum / TRIALS);
            /*
            //ship.getPower();
            //ship.getMaxHp();
            //ship.getSize();
            ship.getReloadSpeed();
            ship.getTurnRate();*/


        }
        out.flush();
        out.close();

    }
}
