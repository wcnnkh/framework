package io.basc.framework.test;

import io.basc.framework.http.HttpUtils;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.http.client.HttpClientErrorException;
import io.basc.framework.http.client.HttpClientResourceAccessException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.http.HttpResponseEntity;

public class HttpClientTest {
	private static Logger logger = LoggerFactory.getLogger(HttpClientTest.class);

	@Test
	public void test() {
		try {
			HttpResponseEntity<String> response = HttpUtils.getClient().get(String.class, "https://www.baidu.com");
			System.out.println(response);
		} catch (HttpClientErrorException | HttpClientResourceAccessException e) {
			// 这些异常可以忽略
			logger.info(e, e.getMessage());
		}
	}

	@Test
	public void test2() {
		HttpClient httpClient = HttpUtils.getClient();
		HttpClient httpClient2 = httpClient.setMaxRedirectDeep(10);
		assertTrue(httpClient != httpClient2);
	}
}
