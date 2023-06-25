package edu.neu.coe.info6205.madhava;
import java.math.BigDecimal;
import java.math.RoundingMode;
public class Madhava{
    public static void main(String[] args) {
        int numTerms = 10000;
        BigDecimal pii = calculatePi(numTerms);
        BigDecimal first=firstTerm( numTerms);
        BigDecimal second=secondTerm( numTerms);
        BigDecimal third=thirdTerm( numTerms);
        BigDecimal fourth=forthTerm( numTerms);
        //System.out.println("pi = " + getSeries(numTerms));
        System.out.println("For the "+numTerms+" number terms the pi values are as follows ");
        System.out.println("Basic pi: " + pii);
        BigDecimal pi= approximatePi(pii,numTerms,fourth);
        System.out.println("Madhava Approximation of pi: " +  pi);
        System.out.println("Corrected Lastterm: " + fourth);
        System.out.println("Error Difference " +pi.subtract(pii));
    }
    public static String getSeries(int terms) {
        StringBuilder series = new StringBuilder("4");
        int sign = -1;
        for (int i = 1; i < terms; i++) {
            int divisor = 2 * i + 1;
            series.append(sign == 1 ? " + " : " - ");
            series.append("4/" + divisor);
            sign *= -1;
        }
        return series.toString();
    }

    public static BigDecimal firstTerm(int numTerms){
        BigDecimal firstterm = BigDecimal.ONE.divide(BigDecimal.valueOf(numTerms), 69, BigDecimal.ROUND_HALF_UP);
        return firstterm;
    }
    public static BigDecimal secondTerm(int numTerms){
        BigDecimal secondterm = BigDecimal.ONE.divide(BigDecimal.valueOf(numTerms).add(BigDecimal.ONE.divide
                (BigDecimal.valueOf(4).multiply(BigDecimal.valueOf(numTerms)))),69,RoundingMode.HALF_UP);
        return secondterm;
    }
    public static BigDecimal thirdTerm(int numTerms){
        BigDecimal thirdterm = BigDecimal.ONE.divide(BigDecimal.valueOf(numTerms).add(BigDecimal.ONE.divide(BigDecimal.valueOf(4).multiply(BigDecimal.valueOf(numTerms)).add(BigDecimal.valueOf(4).divide(BigDecimal.valueOf(numTerms),69,RoundingMode.HALF_UP)),69,RoundingMode.HALF_UP)),69,RoundingMode.HALF_UP);
        return thirdterm;
    }
    public static BigDecimal forthTerm(int numTerms){
        BigDecimal fourthterm = BigDecimal.ONE.divide(BigDecimal.valueOf(numTerms).add(BigDecimal.ONE.divide(BigDecimal.valueOf(numTerms*4).add(BigDecimal.valueOf(4).
                divide(BigDecimal.valueOf(numTerms).add(BigDecimal.valueOf(9).divide(BigDecimal.valueOf(numTerms*4),10,RoundingMode.HALF_UP)
                ),69,RoundingMode.HALF_UP)),69,RoundingMode.HALF_UP)),69,RoundingMode.HALF_UP);
        return fourthterm;
    }
    public static BigDecimal approximatePi(BigDecimal pii,int numTerms,BigDecimal lastTerm){
        if (numTerms % 2 == 0) {
            pii = pii.add(lastTerm);
        } else {
            pii = pii.subtract(lastTerm);
        }
        return pii;
    }
    public static BigDecimal calculatePi(int terms) {
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal sign = BigDecimal.ONE;
        for (int i = 0; i < terms; i++) {
            int divisor = 2 * i + 1;
            BigDecimal term = sign.divide(BigDecimal.valueOf(divisor), 69, RoundingMode.HALF_UP);
            pi = pi.add(term);
            sign = sign.negate();
        }
        return pi.multiply(BigDecimal.valueOf(4));
    }
}