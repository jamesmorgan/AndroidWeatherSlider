package com.morgan.design.android.util;

import static com.morgan.design.Constants.READ_TIMEOUT;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateFactory {

	public static String createAndQuery(final String url) {
		try {
			final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setReadTimeout(READ_TIMEOUT);

			final RestTemplate restTemplate = new RestTemplate();
			restTemplate.setRequestFactory(requestFactory);

			return restTemplate.getForObject(url, String.class);
		}
		catch (final Exception exception) {
			return null;
		}
	}
}
