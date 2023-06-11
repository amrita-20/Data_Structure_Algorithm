package edu.neu.coe.info6205.union_find;

import java.util.Random;

public class UF_Client {
    public static int count(int n){
        Random random = new Random();
        int noOfConnections = 0;
        UF_HWQUPC uf_hwqupc = new UF_HWQUPC(n);
        while (uf_hwqupc.components() > 1){
            int p = random.nextInt(n);
            int q = random.nextInt(n);
            uf_hwqupc.connect(p,q);
            noOfConnections++;
        }
        return noOfConnections;
    }

    public static void main(String[] args){
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 100;
        int runs = args.length > 1 ? Integer.parseInt(args[1]) : 100;
        int j = 1;
        while(j < 10000){
            int mean = 0;
            int k = n*j;
            for(int i=0; i<runs; i++){
                mean += count( k);
            }
            mean = mean/runs;
            j*=2;
            System.out.println("for input N: " + k + ", No of Connections: " + mean);
           // System.out.println(uf_hwqupc.toString());       //just for visualizing the output
        }
    }
}
