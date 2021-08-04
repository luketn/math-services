package com.serverless;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.primes.Primes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import static org.apache.commons.lang3.math.Fraction.getFraction;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(Handler.class);
	public static final String QUERY_PARAMS_MISSING_ERROR = "Please provide a 'left' and 'right' query parameter in fraction form e.g. /math/ratios/fractions/simplify?left=1/2&right=3/4";

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);

		Map<String, String> queryStringParameters = (Map<String, String>) input.get("queryStringParameters");

		int statusCode;
		String message;
		List<String> result;
		if (queryStringParameters == null) {
			result=null;
			statusCode=400;
			message= QUERY_PARAMS_MISSING_ERROR;
		} else {

			String left = queryStringParameters.get("left");
			String right = queryStringParameters.get("right");

			if (StringUtils.isAnyEmpty(left, right)) {
				result = null;
				statusCode = 400;
				message = QUERY_PARAMS_MISSING_ERROR;
			} else {
				String steps = FractionalRatioSimplifier.calculate(left, right);
				message = "Calculated successfully!";
				result = Arrays.asList(steps.split("\n"));
				statusCode = 200;
			}
		}

		Response responseBody = new Response(message, result);

		return ApiGatewayResponse.builder()
				.setStatusCode(statusCode)
				.setObjectBody(responseBody)
				.setHeaders(Collections.singletonMap("X-Powered-By", "MyCodeFu API"))
				.build();
	}

}
