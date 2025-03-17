package io.basc.framework.web.test;

import run.soeasy.framework.http.HttpResponseEntity;
import run.soeasy.framework.http.HttpUtils;
import run.soeasy.framework.http.client.HttpClient;
import run.soeasy.framework.http.client.HttpClientErrorException;
import run.soeasy.framework.http.client.HttpClientResourceAccessException;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HttpClientTest {
	private static Logger logger = LogManager.getLogger(HttpClientTest.class);

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
