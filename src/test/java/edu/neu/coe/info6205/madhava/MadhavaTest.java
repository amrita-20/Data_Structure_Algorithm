package edu.neu.coe.info6205.madhava;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MadhavaTest {

    @Test
    public void testFirstTerm() {
        int numTerms = 10000;
        BigDecimal expected = BigDecimal.ONE.divide(BigDecimal.valueOf(numTerms), 69, BigDecimal.ROUND_HALF_UP);
        BigDecimal actual = Madhava.firstTerm(numTerms);
        assertEquals(expected, actual);
    }

    @Test
    public void testSecondTerm() {
        int numTerms = 10000;
        BigDecimal expected = BigDecimal.ONE.divide(
                BigDecimal.valueOf(numTerms).add(BigDecimal.ONE.divide(
                        BigDecimal.valueOf(4).multiply(BigDecimal.valueOf(numTerms))
                )),
                69,
                RoundingMode.HALF_UP
        );
        BigDecimal actual = Madhava.secondTerm(numTerms);
        assertEquals(expected, actual);
    }
}
