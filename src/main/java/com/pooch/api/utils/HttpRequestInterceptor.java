package com.pooch.api.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response);
		return response;
	}

	private void logRequest(HttpRequest request, byte[] body) {
		// if (log.isDebugEnabled()) {
		try {
			System.out.println();
			System.out.println("===========================Http Request begin=============================================");
			System.out.println("URI         : "+ request.getURI());
			System.out.println("Method      : "+ request.getMethod());
			System.out.println("Headers     : "+ request.getHeaders());
			System.out.println("Request body: "+ new String(body, "UTF-8"));
			System.out.println("===========================Http Request end===============================================");
			System.out.println();
		} catch (Exception e) {
			log.warn("Exception msg: {}",e.getMessage());
		}

		// }
	}

	private String logResponse(ClientHttpResponse response) {
		// if (log.isDebugEnabled()) {
		try {
			System.out.println();
			System.out.println("===========================Http Response begin============================================");
			System.out.println("Status code  : "+ response.getStatusCode());
			System.out.println("Headers      : "+ response.getHeaders());
			
			InputStream inputStream = response.getBody();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
	        reader.close();
			
	        System.out.println("Response body: "+ out.toString());
			System.out.println("===========================Http Response end==============================================");
			System.out.println();
			return out.toString();
		} catch (Exception e) {
			log.warn("Exception msg: {}",e.getMessage());
			return e.getLocalizedMessage();
		}

		// }
	}

}
