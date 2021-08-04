package com.serverless;

import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.primes.Primes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static org.apache.commons.lang3.math.Fraction.getFraction;

public class FractionalRatioSimplifier {
    private static final Logger LOG = LogManager.getLogger(FractionalRatioSimplifier.class);

    public static String calculate(String ratioLeftString, String ratioRightString) {
        Fraction leftFraction = getFraction(ratioLeftString);
        Fraction rightFraction = getFraction(ratioRightString);

        StringBuilder workingOut = new StringBuilder();

        String initialRatio = formatRatio(leftFraction, rightFraction);
        workingOut.append(initialRatio);

        String secondStep;
        if (leftFraction.getDenominator() == rightFraction.getDenominator()) {
            secondStep = "";
        } else if (leftFraction.getDenominator() % rightFraction.getDenominator() == 0) {
            //multiply right numerator by (left denominator/ right denominator)
            int rightNumerator = rightFraction.getNumerator() * (leftFraction.getDenominator() / rightFraction.getDenominator());
            rightFraction = getFraction(
                    rightNumerator,
                    leftFraction.getDenominator());
            secondStep = formatRatio(leftFraction, rightFraction);

        } else if (rightFraction.getDenominator() % leftFraction.getDenominator() == 0) {
            //multiply left numerator by (right denominator/left denominator)
            int leftNumerator = leftFraction.getNumerator() * (rightFraction.getDenominator() / leftFraction.getDenominator());
            leftFraction = getFraction(
                    leftNumerator,
                    rightFraction.getDenominator());
            secondStep = formatRatio(leftFraction, rightFraction);

        } else {
            //multiply left and right numerator by their opposing denominator, and set both denominators to the product of the two
            int leftNumerator = leftFraction.getNumerator() * rightFraction.getDenominator();
            int rightNumerator = rightFraction.getNumerator() * leftFraction.getDenominator();
            int denominator = leftFraction.getDenominator() * rightFraction.getDenominator();
            leftFraction = getFraction(leftNumerator, denominator);
            rightFraction = getFraction(rightNumerator, denominator);
            secondStep = formatRatio(leftFraction, rightFraction);

        }
        workingOut.append(secondStep);

        //Now I have two fractions with equal denominators, which can be cancelled out of the ratio
        int left = leftFraction.getNumerator();
        int right = rightFraction.getNumerator();

        if (left > 1 && right > 1) {
            List<Integer> leftPrimes = Primes.primeFactors(left);
            List<Integer> rightPrimes = Primes.primeFactors(right);

            for (int leftPointer = 0, rightPointer = 0; leftPointer < leftPrimes.size() && rightPointer < rightPrimes.size(); ) {
                Integer leftPrimeValue = leftPrimes.get(leftPointer);
                Integer rightPrimeValue = rightPrimes.get(rightPointer);
                if (leftPrimeValue == rightPrimeValue) {
                    Integer primeValue = leftPrimeValue;
                    left /= primeValue;
                    right /= primeValue;
                    leftPointer++;
                    rightPointer++;
                } else if (leftPrimeValue < rightPrimeValue) {
                    leftPointer++;
                } else {
                    rightPointer++;
                }
            }
        }

        String thirdStep = String.format("%s : %s", left, right);
        workingOut.append(thirdStep).append('\n');

        String result = workingOut.toString();
        LOG.info(result);
        return result;
    }

    private static String formatRatio(Fraction fractionOne, Fraction fractionTwo) {
        return String.format("%s : %s\n", fractionOne, fractionTwo);
    }
}
