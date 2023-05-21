/*
 * Copyright (c) 2017. Phasmid Software
 */

package edu.neu.coe.info6205.randomwalk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class RandomWalk {

    private int x = 0;
    private int y = 0;

    private final Random random = new Random();

    /**
     * Private method to move the current position, that's to say the drunkard moves
     *
     * @param dx the distance he moves in the x direction
     * @param dy the distance he moves in the y direction
     */
    private void move(int dx, int dy) {
        x+=dx;
        y+=dy;
    }

    /**
     * Perform a random walk of m steps
     *
     * @param m the number of steps the drunkard takes
     */
    private void randomWalk(int m) {
        for(int i=0; i<m; i++){
            randomMove();
        }
    }

    /**
     * Private method to generate a random move according to the rules of the situation.
     * That's to say, moves can be (+-1, 0) or (0, +-1).
     */
    private void randomMove() {
        boolean ns = random.nextBoolean();
        int step = random.nextBoolean() ? 1 : -1;
        move(ns ? step : 0, ns ? 0 : step);
    }

    /**
     * Method to compute the distance from the origin (the lamp-post where the drunkard starts) to his current position.
     *
     * @return the (Euclidean) distance from the origin to the current position.
     */
    public double distance() {
         return Math.sqrt((x*x) +(y*y));
    }

    /**
     * Perform multiple random walk experiments, returning the mean distance.
     *
     * @param m the number of steps for each experiment
     * @param n the number of experiments to run
     * @return the mean distance
     */
    public static double randomWalkMulti(int m, int n) {
        double totalDistance = 0;
        for (int i = 0; i < n; i++) {
            RandomWalk walk = new RandomWalk();
            walk.randomWalk(m);
            totalDistance = totalDistance + walk.distance();
        }
        return totalDistance / n;
    }

    public static void main(String[] args) {
      /*  if (args.length == 0)
            throw new RuntimeException("Syntax: RandomWalk steps [experiments]");
        int m = Integer.parseInt(args[0]);
        int n = 30;
        if (args.length > 1) n = Integer.parseInt(args[1]);
        double meanDistance = randomWalkMulti(m, n);
        System.out.println(m + " steps: " + meanDistance + " over " + n + " experiments");
*/
        // Second approach

        int n = 1000;      // Number of experiments for each step size
        StringBuilder sb = new StringBuilder("Steps, MeanDistance\n");
        for(int i=1; i<10000; i++){
            //double m = Math.pow(2, i);
            int m = 2*i;
            double meanDistance = randomWalkMulti(m, n);
            sb.append(m  + "," + meanDistance+"\n");     // Append the step size and mean distance to the StringBuilder
            System.out.println(m + " steps: " + meanDistance + " over " +  n + " experiments");
        }

        // Write the results to a CSV file named "result.csv"
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("result.csv"));
            bw.write(sb.toString());
            bw.close();
        } catch(Exception e) {
            System.out.println("Error in creating result file"+e);
        }
    }

}
