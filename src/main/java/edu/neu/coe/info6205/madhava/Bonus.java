package edu.neu.coe.info6205.madhava;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Bonus {
    public static void main(String[] args) {
        int numTerms = 20000;
        BigDecimal pii = calculatePi(numTerms);
        System.out.println("For the "+numTerms+" number terms the pi values are as follows ");
        System.out.println("Basic pi: " + pii);
        BigDecimal pi= neelkanthPi(numTerms);
        System.out.println("Neelkanth Approximation of pi: " +  pi);
        System.out.println("Error Difference " +pi.subtract(pii));
    }

    public static BigDecimal neelkanthPi(int terms){
        BigDecimal pi = BigDecimal.valueOf(3), n = BigDecimal.valueOf(2), sign = BigDecimal.valueOf(1);
        for (int i = 0; i <= terms; i++) {
            pi = pi.add(sign.multiply(BigDecimal.valueOf(4).divide(
                    n.multiply(n.add(BigDecimal.ONE)).multiply(n.add(BigDecimal.valueOf(2))),30,RoundingMode.HALF_UP)));

            sign = sign.multiply(BigDecimal.valueOf(-1));
            n = n.add(BigDecimal.valueOf(2));
        }
        return pi;
    }
    public static BigDecimal calculatePi(int terms) {
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal sign = BigDecimal.ONE;
        for (int i = 0; i < terms; i++) {
            int divisor = 2 * i + 1;
            BigDecimal term = sign.divide(BigDecimal.valueOf(divisor), 30, RoundingMode.HALF_UP);
            pi = pi.add(term);
            sign = sign.negate();
        }
        return pi.multiply(BigDecimal.valueOf(4));
    }
}