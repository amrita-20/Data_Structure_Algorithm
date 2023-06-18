package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * CONSIDER tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
        for(int p=2; p<=128; p*=2){
            test(new String[]{"-N", Integer.toString(10000), "-P", Integer.toString(p)});
        }
    }

    public static void test(String[] args) {
        processArgs(args);
        int noOfThread = configuration.get("-P");
        int cutOff = configuration.get("-N");
        ParSort.maxDepth = Math.log(noOfThread)/Math.log(2);
        ParSort.myPool = new ForkJoinPool(noOfThread);
        System.out.println("Degree of parallelism: " + ParSort.myPool.getParallelism());
        Random random = new Random();
        int[] array = new int[5120000];
        ArrayList<Long> timeList = new ArrayList<>();
        for (int j = 1; j < 1000 ; j*=2) {
            ParSort.cutoff = cutOff * j ;
            long time;
            long startTime = System.currentTimeMillis();
            for (int t = 0; t < 10; t++) {
                for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                ParSort.sort(array, 0, array.length, 0);
            }
            long endTime = System.currentTimeMillis();
            time = (endTime - startTime);
            timeList.add(time);

           // System.out.println((ParSort.cutoff) + "\t" + noOfThread +"\t"+ ParSort.maxDepth +"\t" + (double)time/10);
            System.out.println((ParSort.cutoff) + "\t" + (double)time/10);
        }

        try {
            FileOutputStream fis = new FileOutputStream("./src/result.csv");
            OutputStreamWriter isr = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(isr);
            int j = 0;
            for (long i : timeList) {
                String content = (double) 10000 * (j + 1) / 2000000 + "," + (double) i / 10 + "\n";
                j++;
                bw.write(content);
                bw.flush();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[xs.length-2];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("-N")) setConfig(x, Integer.parseInt(y));
        else
            if (x.equalsIgnoreCase("-P")) {
                setConfig(x, Integer.parseInt(y));
            }
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();
}
