package edu.neu.coe.info6205.madhava;

import com.phasmidsoftware.number.core.Rational;
import com.phasmidsoftware.number.core.Rational$;
import scala.math.BigInt;

import java.math.BigInteger;

public class Wallis {
    public static Rational halfPi(int n) {
        Rational result = Rational$.MODULE$.apply(1);
        for (int i = 1; i <= n; i++)
            result = result.$times(term(i));
        return result;
    }

    public static BigNumber pi(int n) {
        Rational pi = halfPi(n).$times(2);
        return BigNumber.value(pi);
    }

    public static Rational term(long i) {
        BigInteger twoI = BigInteger.valueOf(2 * i);
        BigInteger denom = twoI.subtract(BigInteger.ONE).multiply(twoI.add(BigInteger.ONE));
        return new Rational(convertToBigInt(twoI.multiply(twoI)), convertToBigInt(denom));
    }

    public static BigInt convertToBigInt(BigInteger x) {
        return new BigInt(x);
    }

    public static BigInteger convertFromBigInt(BigInt x) {
        return x.underlying();
    }

    public static void main(String[] args) {
        int numTerms = 10000; // Set the number of terms for the Wallis product
        long st = System.currentTimeMillis();
        BigNumber pi = pi(numTerms);
        long et = System.currentTimeMillis();
        long time = et - st;
        System.out.println("Approximation of pi: " + pi.toString().substring(0, 102));
        System.out.println("Time in second: " + (double)time/1000);
    }
}
