package com.serverless;

import java.util.List;
import java.util.Map;

public class Response {

	private final String message;
	private final List<String> result;

	public Response(String message, List<String> result) {
		this.message = message;
		this.result = result;
	}

	public String getMessage() {
		return this.message;
	}

	public List<String> getResult() {
		return this.result;
	}
}
