package com.serverless;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.primes.Primes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import static org.apache.commons.lang3.math.Fraction.getFraction;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(Handler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);

		Map<String, String> queryStringParameters = (Map<String, String>) input.get("queryStringParameters");

		String left = queryStringParameters.get("left");
		String right = queryStringParameters.get("right");

		String steps = calculate(left, right);

		Response responseBody = new Response("Calculated successfully!", Collections.singletonMap(
				"value", steps.split("\n")
		));

		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(responseBody)
				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
				.build();
	}


	public String calculate(String ratioLeftString, String ratioRightString) {
		Fraction leftFraction = getFraction(ratioLeftString);
		Fraction rightFraction = getFraction(ratioRightString);

		StringBuilder workingOut = new StringBuilder();

		String initialRatio = formatRatio(leftFraction, rightFraction);
		LOG.info(initialRatio);
		workingOut.append(initialRatio).append('\n');

		String secondStep;
		if (leftFraction.getDenominator() % rightFraction.getDenominator() == 0) {
			//multiply right numerator by (left denominator/ right denominator)
			int rightNumerator = rightFraction.getNumerator() * (leftFraction.getDenominator() / rightFraction.getDenominator());
			leftFraction = getFraction(
					rightNumerator,
					rightFraction.getDenominator());
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
		workingOut.append(secondStep).append('\n');

		//Now I have two fractions with equal denominators, which can be cancelled out of the ratio
		int left = leftFraction.getNumerator();
		int right = rightFraction.getNumerator();

		List<Integer> leftPrimes = Primes.primeFactors(left);
		List<Integer> rightPrimes = Primes.primeFactors(right);

		for (int leftPointer=0, rightPointer=0; leftPointer < leftPrimes.size() && rightPointer < rightPrimes.size(); ) {
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

		String thirdStep = String.format("%s : %s", left, right);
		workingOut.append(thirdStep).append('\n');

		return workingOut.toString();
	}

	private String formatRatio(Fraction fractionOne, Fraction fractionTwo) {
		return String.format("%s : %s", fractionOne, fractionTwo);
	}
}
