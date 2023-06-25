package edu.neu.coe.info6205.madhava;

import com.phasmidsoftware.number.core.Rational;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BigNumber class which represents an exact number of unlimited decimal extent.
 * The representation of the whole part is a BigInteger.
 * The representation of the decimal part is as a true decimal (not binary) number.
 */
public class BigNumber extends Number {
    /**
     * Some BigNumber constants.
     */
    public static final BigNumber zero = new BigNumber(0);
    public static final BigNumber one = new BigNumber(1);
    public static final BigNumber ten = new BigNumber(10);
    public static final BigNumber two = new BigNumber(2);
    // NOTE that the following is an exact number. It is defined as the closest approximation to pi with 100 digits.
    // But, we know that it is not pi itself.
    public static final BigNumber pi = BigNumber.parse("3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067");

    /**
     * Factory method to throw an exception.
     *
     * @param ignoredX a double.
     * @throws BigNumberException because BigNumber is an EXACT representation whereas double is not.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static BigNumber value(double ignoredX) {
        throw new BigNumberException("value(double): This is an inappropriate operation. Please use parse or another value call.");
    }

    /**
     * Factory method to convert a long into a BigNumber.
     *
     * @param x a long.
     * @return a new BigNumber.
     */
    public static BigNumber value(long x) {
        return value(BigInteger.valueOf(x));
    }

    /**
     * Factory method to create a BigNumber from an (exact) BigDecimal.
     *
     * @param x a BigDecimal number.
     * @return a new BigNumber.
     */
    public static BigNumber value(BigDecimal x) {
        BigNumber result = BigNumber.value(x.unscaledValue().longValue());
        return result.divide(BigInteger.TEN.pow(x.scale()));
    }

    /**
     * Factory method to create a BigNumber from a Rational (a Scala class).
     *
     * @param x a Rational number.
     * @return a new BigNumber which may or may not be exact.
     */
    public static BigNumber value(Rational x) {
        BigNumber n = BigNumber.value(x.n().bigInteger());
        BigNumber d = BigNumber.value(x.d().bigInteger());
        return n.divide(d);
    }

    /**
     * Factory method to create a BigNumber from a long whole number and a long representing the decimals.
     *
     * @param whole    a non-negative long.
     * @param decimals a non-negative long, for example 14 for a 3.14 valued result.
     * @param sign     true if the result should be positive.
     * @return a new BigNumber.
     */
    public static BigNumber value(long whole, long decimals, boolean sign) {
        if (whole < 0) throw new RuntimeException("value: whole must be non-negative");
        if (decimals < 0) throw new RuntimeException("value: decimals must be non-negative");
        int[] dec = new int[MAXLONGDIGITS];
        int i = dec.length;
        long x = decimals;
        while (x > 0) {
            int q = (int) (x % 10);
            dec[--i] = q;
            x = x / 10;
        }
        return new BigNumber(BigInteger.valueOf(Math.abs(whole)), Arrays.copyOfRange(dec, i, MAXLONGDIGITS), sign);
    }

    /**
     * Factory method to create a BigNumber from a whole number and a decimal number.
     *
     * @param whole    a non-negative long.
     * @param decimals a non-negative long, for example 14 for a 3.14 valued result.
     * @return a new BigNumber.
     */
    public static BigNumber value(long whole, long decimals) {
        return value(whole, decimals, true);
    }

    /**
     * Factory method to create a BigNumber from x.
     *
     * @param x a BigInteger.
     * @return a new BigNumber.
     */
    public static BigNumber value(BigInteger x) {
        return new BigNumber(x);
    }

    /**
     * Factory method to create a BigNumber from a whole number and a decimal number.
     *
     * @param s a String representing a BigNumber. It is optionally preceded by a "-" and, otherwise, is of form "x.y"
     * @return a new BigNumber.
     */
    public static BigNumber parse(String s) {
        Pattern p = Pattern.compile("(-?)(\\d+)(\\.?(\\d*)?)");
        Matcher matcher = p.matcher(s);
        if (matcher.find()) {
            String group4 = matcher.group(4);
            int decimals = group4.length();
            int[] dec = new int[decimals];
            for (int i = 0; i < decimals; i++) dec[i] = group4.charAt(i) - '0';
            return new BigNumber(BigInteger.valueOf(Long.parseLong(matcher.group(2))), dec, !matcher.group(1).equals("-"));
        } else throw new BigNumberException("cannot parse input string: " + s);
    }

    /**
     * Method to determine if this BigNumber is a whole number.
     *
     * @return true if this BigNumber is a whole number.
     */
    public boolean isWhole() {
        return decimals.length == 0;
    }

    /**
     * Method to determine if this BigNumber is, as expected, exact.
     * The reason it might not be exact is that we allow division to yield a BigNumber.
     * For now at least, we simply look at the number of decimals.
     * CONSIDER in future we might have a field which explicitly determines exactness.
     *
     * @return true if this BigNumber is exact.
     */
    public boolean isExact() {
        return decimals.length < MAXDECIMALDIGITS;
    }

    /**
     * Return a BigDecimal which has the exact value of this BigNumber.
     *
     * @return a BigDecimal.
     */
    public BigDecimal toBigDecimal() {
        int scale = decimals.length;
        BigInteger value = whole;
        for (int decimal : decimals) {
            value = value.multiply(BigInteger.TEN).add(BigInteger.valueOf(decimal));
        }
        BigDecimal result = new BigDecimal(value, scale);
        return sign ? result : result.negate();
    }

    /**
     * Returns the value of the specified number as an {@code int}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code int}.
     */
    @Override
    public int intValue() {
        if (isWhole()) {
            int value = whole.intValueExact(); // NOTE: can throw an ArithmeticException
            return sign ? value : -value;
        } else throw new BigNumberException("intValue: cannot represent this BigNumber as an int: " + this);
    }

    /**
     * Returns the value of the specified number as a {@code long}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code long}.
     */
    @Override
    public long longValue() {
        if (isWhole()) {
            long value = whole.longValueExact(); // NOTE: can throw an ArithmeticException
            return sign ? value : -value;
        } else throw new BigNumberException("longValue: cannot represent this BigNumber as a long: " + this);
    }

    /**
     * Returns the value of the specified number as a {@code float}.
     *
     * @return the numeric value represented by this object after conversion
     * to type {@code float}.
     */
    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    /**
     * Method to yield a double representation of this BigNumber.
     * In general, the result will NOT be exact.
     * But you can check on that by using the isExact() method.
     * CONSIDER It is possible that isExact() will be false but the resulting double will be exact.
     *
     * @return a double representation of this.
     */
    public double doubleValue() {
        double result = whole.doubleValue();
        double factor = 1.0 / 10;
        for (int decimal : decimals) {
            result += factor * decimal;
            factor /= 10;
        }
        return sign ? result : -result;
    }

    public BigNumber add(BigNumber that) {
        if (!sign && !that.sign) return this.negate().add(that.negate()).negate();
        if (!sign) return that.add(this);
        if (this.compareTo(that) < 0) {
            return that.negate().add(this.negate()).negate();
        }
        // NOTE at this point, sign is always true.
        int thisLength = decimals.length;
        int thatLength = that.decimals.length;
        int resultLength = Math.max(thisLength, thatLength);
        int[] dec = new int[resultLength];
        int carry = 0;
        boolean borrow = false;
        boolean subtract = !that.sign;
        for (int i = resultLength - 1; i >= 0; i--) {
            int sum = carry;
            borrow = false;
            if (i < thisLength) sum += decimals[i];
            if (i < thatLength) sum += subtract ? -that.decimals[i] : that.decimals[i];
            if (sum < 0) {
                sum += 10;
                if (i != 0) dec[i - 1] -= 1;
                else borrow = true;
            }
            carry = sum / 10;
            dec[i] += sum % 10;
        }
        BigInteger wholeSum = whole.add((subtract ? that.whole.negate() : that.whole).add(BigInteger.valueOf(carry)));
        if (borrow) wholeSum = wholeSum.add(BigInteger.valueOf(-1));
        if (wholeSum.signum() < 0) {
            for (int i = 0; i < resultLength; i++) dec[i] = -dec[i];
            return new BigNumber(wholeSum.negate(), dec, false);
        }
        return new BigNumber(wholeSum, dec, true);
    }

    public int compareTo(BigNumber that) {
        if (this.equals(that)) {
            return 0;
        } else {
            int wholeComparison = this.whole.compareTo(that.whole);
            if (wholeComparison != 0) {
                return this.sign ? wholeComparison : -wholeComparison;
            } else {
                int[] thisDecimals = this.decimals;
                int[] thatDecimals = that.decimals;
                int maxLength = Math.max(thisDecimals.length, thatDecimals.length);
                for (int i = 0; i < maxLength; i++) {
                    int thisNumber = (i < thisDecimals.length) ? thisDecimals[i] : 0;
                    int thatNumber = (i < thatDecimals.length) ? thatDecimals[i] : 0;
                    if (thisNumber != thatNumber) {
                        return this.sign ? Integer.compare(thisNumber, thatNumber)
                                : Integer.compare(thatNumber, thisNumber);
                    }
                }
                return 0;
            }
        }
    }

    public BigNumber negate() {
        return new BigNumber(whole, decimals, !sign);
    }

    /**
     * Method to multiply Big Numbers using Karatsuba Algorithm.
     *
     * @param other a BigNumber value.
     * @return a BigNumber.
     */
    public BigNumber multiplyWithKaratsuba(BigNumber other) {
        BigNumber thisW = BigNumber.value(this.whole);
        BigNumber thatW = BigNumber.value(other.whole);

        if (this.isWhole() && other.isWhole())
            return thisW.multiply(thatW);

        int[] first = this.decimals;
        int[] second = other.decimals;

        if (first.length < second.length) {
            first = Arrays.copyOf(first, second.length);
        } else {
            second = Arrays.copyOf(second, first.length);
        }

        BigNumber thisF = new BigNumber(BigInteger.ZERO, first, true);
        BigNumber thatF = new BigNumber(BigInteger.ZERO, second, true);
        BigNumber result1 = thisW.multiply(thatW);
        BigNumber result2 = thisW.multiply(thatF);
        BigNumber result3 = thatW.multiply(thisF);

        int[] result = recursiveKarat(first, second, 0, first.length - 1, 2 * first.length);
        BigNumber result4 = new BigNumber(BigInteger.ZERO, result, true);
        return  result1.add(result2).add(result3).add(result4);
    }

    private int[] multiplyArrays(int[] arr1, int[] arr2, int start, int end, int resultSize) {
        StringBuilder b = new StringBuilder();
        for (int i = start; i <= end; i++) {
            b.append(arr1[i]);
        }
        BigInteger b1 = new BigInteger(b.toString());

        b.setLength(0);
        for (int i = start; i <= end; i++) {
            b.append(arr2[i]);
        }
        BigInteger b2 = new BigInteger(b.toString());

        String res = b2.multiply(b1).toString();
        int[] result = new int[resultSize];

        int resIdx = resultSize - 1;
        for (int i = res.length() - 1; i >= 0; i--) {
            result[resIdx--] = res.charAt(i) - 48;
        }
        return result;
    }

    private int[] recursiveKarat(int[] first, int[] second, int start, int end, int resSize) {
        // adaptively ignore leading zeros
        for (int i = start; i <= end; i++) {
            if (first[i] != 0 || second[i] != 0) {
                start = i;
                break;
            }
        }

        int numDigits = end - start + 1;
        if (numDigits == 0) {
            return new int[resSize];
        }
        if (numDigits <= 160) {
            // return an array of size `resSize`
            return multiplyArrays(first, second, start, end, resSize);
        }

        int middle = (end + start) / 2;
        int m = end - middle;
        int[] z0 = recursiveKarat(first, second, start, middle, resSize);
        int[] z2 = recursiveKarat(first, second, middle + 1, end, resSize);

        int[] sum1 = add(first, start, middle, end, numDigits + 1);
        int[] sum2 = add(second, start, middle, end, numDigits + 1);
        int[] prod = recursiveKarat(sum1, sum2, 0, sum1.length - 1, 2 * numDigits);
        difference(prod, z0);
        difference(prod, z2);

        leftShift(prod, m);
        leftShift(z0, 2 * m);

        addInPlace(z0, prod);
        addInPlace(z0, z2);
        return z0;
    }

    private void leftShift(int[] arr, int offset) {
        int i = 0;
        while (i < arr.length && arr[i] == 0) {
            i++;
        }
        for (; i < arr.length; i++) {
            arr[i - offset] = arr[i];
            arr[i] = 0;
        }
    }

    private int[] add(int[] first, int start, int middle, int end, int resSize) {
        int[] result = new int[resSize];
        int carry = 0;
        int val, toAdd;
        int k = resSize - 1;
        for (int i = end, j = middle; j >= start; i--, j--, k--) {
            if (i <= middle) {
                toAdd = 0;
            } else {
                toAdd = first[i];
            }
            val = toAdd + first[j] + carry;
            if (val >= 10) {
                carry = 1;
                result[k] = val - 10;
            } else {
                carry = 0;
                result[k] = val;
            }
        }
        result[k] = carry;
        return result;
    }

    private void addInPlace(int[] first, int[] second) {
        int j = first.length - 1;
        int carry = 0;
        int val;
        for (int i = first.length - 1, k = second.length - 1; k >= 0; i--, k--, j--) {
            val = first[i] + second[k] + carry;
            first[j] = val % 10;
            carry = val / 10;
        }
        if (carry != 0) {
            first[j] += carry;
        }
    }

    private void difference(int[] first, int[] second) {
        for (int i = first.length - 1, j = second.length - 1; i >= 0; i--, j--) {
            if (first[i] >= second[j]) {
                first[i] -= second[j];
            } else {
                first[i] = first[i] + 10 - second[j];
                first[i - 1]--; // carry
            }
        }
    }

    public BigNumber multiply(BigNumber that) {
        BigInteger bigTen = BigInteger.valueOf(10);
        int thisLength = decimals.length + 1, thatLength = that.decimals.length + 1;
        long[] results = new long[thisLength + thatLength];
        int[] dec = new int[thisLength + thatLength - 1];
        for (int i = 0; i < thisLength; i++)
            for (int j = 0; j < thatLength; j++)
                results[i + j] += element(i) * that.element(j);
        BigInteger carry = BigInteger.ZERO;
        for (int k = results.length; k > 1; k--) {
            BigInteger value = carry.add(BigInteger.valueOf(results[k - 1]));
            BigInteger[] qr = value.divideAndRemainder(bigTen);
            carry = qr[0];
            dec[k - 2] = qr[1].intValue();
        }
        return new BigNumber(carry.add(BigInteger.valueOf(results[0])), dec, sign == that.sign);
    }

    /**
     * Method to divide this BigNumber by a BigNumber.
     *
     * @param x a BigNumber value.
     * @return a BigNumber y such that x * y = this.
     */
    public BigNumber divide(BigNumber x) {
        if (x.decimals.length > 0) {
            int n = x.decimals.length;
            BigNumber target = this.multiply(BigNumber.value(BigInteger.TEN.pow(n)));
            BigNumber divisor = x.multiply(BigNumber.value(BigInteger.TEN.pow(n)));
            BigNumber result = target.divide(divisor);
            return x.sign ? result : result.negate();
        } else {
            BigNumber quotient = divide(x.whole);
            return x.sign ? quotient : quotient.negate();
        }
    }

    /**
     * Method to divide this BigNumber by a BigInteger.
     *
     * @param x a BigInteger value.
     * @return a BigNumber y such that x * y = this.
     */
    public BigNumber divide(BigInteger x) {
        if (x.signum() < 0) return divide(x.negate()).negate();
        BigInteger[] quotientRemainder = whole.divideAndRemainder(x);
        BigInteger remainder = quotientRemainder[1];
        List<BigInteger> dividends = new ArrayList<>();
        BigInteger ten = BigInteger.valueOf(10);
        for (int i = 0; i < MAXDECIMALDIGITS; i++) {
            BigInteger p = remainder.multiply(ten).add(BigInteger.valueOf(i < decimals.length ? decimals[i] : 0));
            BigInteger[] qr = p.divideAndRemainder(x);
            remainder = qr[1];
            dividends.add(qr[0]);
            if (remainder.signum() == 0 && i > decimals.length) break;
        }
        int[] dec = new int[dividends.size()];
        for (int i = 0; i < dec.length; i++)
            dec[i] = (int) dividends.get(i).longValue();
        return new BigNumber(quotientRemainder[0], dec, true);
    }

    /**
     * Method to divide this BigNumber by a long.
     * The implementation is based on division by BigInteger.
     *
     * @param x a long value.
     * @return a BigNumber y such that x * y = this.
     */
    public BigNumber divide(long x) {
        return divide(BigInteger.valueOf(x));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BigNumber)) return false;
        BigNumber bigNumber = (BigNumber) o;
        return whole.equals(bigNumber.whole) && sign == bigNumber.sign && Arrays.equals(decimals, bigNumber.decimals);
    }

    /**
     * TESTME
     *
     * @return the hashCode for this BigNumber.
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(whole, sign);
        result = 31 * result + Arrays.hashCode(decimals);
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder(sign ? "" : "-");
        result.append(whole);
        String dec = decimalsToString();
        if (dec.length() > 0) result.append('.');
        result.append(dec);
        return result.toString();
    }

    /**
     * Primary constructor of BigNumber.
     *
     * @param whole    the whole number part (must be non-negative).
     * @param decimals the array of decimal digits.
     * @param sign     true if the result is to a positive number.
     */
    public BigNumber(BigInteger whole, int[] decimals, boolean sign) {
        if (whole.signum() < 0) throw new RuntimeException("BigNumber constructor: whole must be non-negative");
        this.whole = whole;
        this.decimals = trim(decimals);
        this.sign = sign;
    }

    /**
     * Secondary constructor to creat a BigNumber which has no decimal part.
     *
     * @param whole any BigInteger value (positive or negative).
     */
    public BigNumber(BigInteger whole) {
        this(whole.abs(), new int[]{}, whole.signum() >= 0);
    }

    /**
     * Secondary constructor to creat a BigNumber which has no decimal part.
     *
     * @param whole any BigInteger value (positive or negative).
     */
    public BigNumber(long whole) {
        this(BigInteger.valueOf(whole));
    }

    private String decimalsToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int x : decimals) stringBuilder.append(x);
        return stringBuilder.toString();
    }

    private static int[] trim(int[] array) {
        int i = array.length;
        for (; i > 0; i--) if (array[i - 1] != 0) break;
        return Arrays.copyOf(array, i);
    }

    private long element(int i) {
        if (i == 0) return whole.longValueExact();
        else if (i > 0) return decimals[i - 1];
        else return 0; // TESTME CONSIDER throwing an exception.
    }

    /**
     * This number is the maximum number of decimal digits that will be generated by division.
     * If the actual number of decimal digits is less than this number, we assume that the
     * number is exact.
     * CONSIDER looking for repeated sequences to shorten this.
     */
    private static final int MAXDECIMALDIGITS = 1000;
    /**
     * This is the greatest number of digits that a long number can have.
     */
    public static final int MAXLONGDIGITS = 19;

    private final BigInteger whole;
    private final int[] decimals;
    private final boolean sign;

    static class BigNumberException extends RuntimeException {
        public BigNumberException(String s) {
            super(s);
        }
    }

    public static void main(String[] args) {
        String seed = "3.80264732771409300520864834438671209698720957589958651119907375222849430002967817359000866255197344412894598244154180957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418409575899586511199073752228494300029678173590008662551973444128945982441541840957589958651119907375222849430002967817359000866255197344412894598244154184095758995865111990737522284943000296781735900086625519734441289459824415418448117483823635540800639306781065132028198662321859262596212005648226134579249871801970850380282492694242171297757609737902183514884179014706720272008148141514118681922046978558050713877551342513339470002439445599760202735750907375222849430002967817359000866255197344412894598244154184811748382363554080063930678106513202819866232185926259621200564822613457924987180197085038028249269424217129775760973790218351488417901470672027200814814151411868192204697855805071387755134251333947000243944559976020273575090737522284943000296781735900086625519734441289459824415418481174838236355408006393067810651320281986623218592625962120056482261345792498718019708503802824926942421712977576097379021835148841790147067202720081481415141186819220469785580507138775513425133394700024394455997602027357506449179433722141754241393658320727321911046813620076340538390386873407571606319347045589812102646318413325303681054856";
        System.out.println("Seed length: " + seed.length());
        String seed1 = seed.substring(0, 800);
        BigNumber b = parse(seed1);
        System.out.println(seed1.length());
        long [] arr = new long[10];
        for(int i=0; i<10; i++){
            long st = System.currentTimeMillis();
            BigNumber res = b.multiply(b);
            long et= System.currentTimeMillis();
            long time = et-st;
            arr[i] = time;
        }
        long averageTime = 0;
        for(long time : arr){
            averageTime+=time;
        }
        double totalTime = (double)averageTime/10;
        System.out.println("Standard: " + totalTime);



        long [] arr1 = new long[10];
        for(int i=0; i<10; i++){
            long st = System.currentTimeMillis();
            BigNumber res = b.multiplyWithKaratsuba(b);
            long et= System.currentTimeMillis();
            long time = et-st;
            arr1[i] = time;
        }
        long averageTime2 = 0;
        for(long time : arr1){
            averageTime2+=time;
        }
        double totalTime2 = (double)averageTime2/10;
        System.out.println("Karatsuba: " + totalTime2);

        System.out.println(((totalTime-totalTime2)/totalTime)*100);

    }
}
