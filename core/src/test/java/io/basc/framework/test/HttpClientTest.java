package io.basc.framework.test;

import io.basc.framework.http.HttpUtils;

import io.basc.framework.http.HttpResponseEntity;

public class HttpClientTest {
	public static void main(String[] args) {
		HttpResponseEntity<String> response = HttpUtils.getHttpClient().get(String.class, "https://www.baidu.com");
		System.out.println(response);
	}
}
