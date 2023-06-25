package edu.neu.coe.info6205.madhava;
import com.phasmidsoftware.number.core.Rational;
import scala.math.BigInt;
import java.math.BigInteger;


public class WallisBigNumber {
    public static BigNumber halfPi(int n){
        BigNumber result = BigNumber.one;
        for(int i=1; i<=n; i++){
            result = result.multiply(term(i));
        }
        return result;
    }

    public static BigNumber pi(int n){
        BigNumber pi = halfPi(n).multiply(BigNumber.two);
        return pi;
    }

    public static BigNumber term(long i) {
        BigInteger twoI = BigInteger.valueOf(2 * i);
        BigInteger denom = twoI.subtract(BigInteger.ONE).multiply(twoI.add(BigInteger.ONE));
        Rational r = new Rational(convertToBigInt(twoI.multiply(twoI)), convertToBigInt(denom));
        return BigNumber.value(r);
    }

    public static BigInt convertToBigInt(BigInteger x) {
        return new BigInt(x);
    }


    public static void main(String[] args) {
        int numTerms = 10; // Set the number of terms for the Wallis product
        long st = System.currentTimeMillis();
        BigNumber pi = pi(numTerms);
        long et = System.currentTimeMillis();
        long time = et - st;
        System.out.println("Approximation of pi: " + pi.toString().substring(0, 102));
        System.out.println("Time in second: " + (double)time/1000);
    }
}

