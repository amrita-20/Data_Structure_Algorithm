package edu.neu.coe.info6205.sort.elementary;
import edu.neu.coe.info6205.util.Benchmark_Timer;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;
import static edu.neu.coe.info6205.sort.elementary.InsertionSort.DESCRIPTION;

public class InsertionMain {

    public static void swap(Integer[] input, int i, int j){
        int temp = input[i];
        input[i] = input[j];
        input[j] = temp;
    }

    public static Supplier<Integer[]> intsSupplier(int safetyFactor, int n, String dataType) {
        Random r = new Random();
        return () -> {
            Integer [] input = new Integer[n];
            int len = input.length;
            for(int i = 0 ; i < n ; i++)
            {
                input[i] = r.nextInt(n*safetyFactor);
            }
            switch (dataType.toLowerCase()){
                case "random":
                    return input;
                case "ordered":
                    Arrays.sort(input);
                    return input;
                case "partially_ordered":
                    Arrays.sort(input);
                    for (int i = len/4; i < len / 2; i++) {
                        swap(input, i, len-1-i);
                    }
                    return input;
                case "reverse_ordered":
                    Arrays.sort(input);
                    for (int i = 0; i < len / 2; i++) {
                        swap(input, i, len-1-i);
                    }
                    return input;
                default:
                    throw new IllegalArgumentException("Unsupported dataType: " + dataType);
            }
        };
    }

    public static void main(String... args){
        String[] dataType = {"random", "ordered", "partially_ordered", "reverse_ordered"};
        for(int i=0; i<dataType.length; i++){
            int size = 500;
            for(int j = 0; j <= 15; j ++){
                Supplier<Integer[]> array = intsSupplier(10, size, dataType[i]);
                final double t1 = new Benchmark_Timer<Integer[]>(
                        DESCRIPTION,
                        (xs) -> new InsertionSort<Integer>().sort(xs, 0, xs.length - 1)
                ).runFromSupplier(array, 100);
                //System.out.println("Type of array passed " +dataType[i]+ ": " + Arrays.toString(array.get()));
                if(j > 9){
                    System.out.println("Average time per repetition for size : " + size +" with: " +dataType[i]+ " data:" + t1 + " milliseconds");
                    size*=2;
                }
            }
        }
    }
}
