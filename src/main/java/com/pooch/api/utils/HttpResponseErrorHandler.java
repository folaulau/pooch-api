package com.pooch.api.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpResponseErrorHandler implements ResponseErrorHandler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean hasError(ClientHttpResponse response) {
		try {
			System.out.println("===========================Http Error Response begin============================================");
			System.out.println("Status code  : "+ response.getStatusCode());
			System.out.println("Status text  : "+ response.getStatusText());
			System.out.println("Headers      : "+ response.getHeaders());
			System.out.println("Response body: "+ ObjectUtils.getObjectMapper().writeValueAsString(response.getBody()));
			System.out.println("===========================Http Error Response end==============================================");
		} catch (Exception e) {
			log.warn("Exception msg: {}",e.getMessage());
		}
		return false;
	}

	@Override
	public void handleError(ClientHttpResponse response) {
		if (log.isInfoEnabled()) {
			try {
				System.out.println();
				System.out.println(
						"===========================Http HandleError Response begin============================================");
				System.out.println("Status code  :" + response.getStatusCode());
				System.out.println("Status text  :" + response.getStatusText());
				System.out.println("Headers      :" + response.getHeaders());

				InputStream inputStream = response.getBody();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder out = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					out.append(line);
				}
				reader.close();

				System.out.println("Response body: " + out.toString());
				System.out.println(
						"===========================Http HandleError Response end==============================================");
				System.out.println();
			} catch (Exception e) {
				log.warn("Exception msg: {}", e.getMessage());
			}

		}
	}

}
