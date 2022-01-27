package io.basc.framework.http.client;

import io.basc.framework.env.Sys;
import io.basc.framework.event.Observable;

public class HttpClientConfigAccessor {
	private static final Observable<Integer> DEFAULT_CONNECT_TIMEOUT = Sys.env
			.getObservableValue("io.basc.framework.http.client.connect.timeout", Integer.class, 10000);
	private static final Observable<Integer> DEFAULT_READ_TIMEOUT = Sys.env
			.getObservableValue("io.basc.framework.http.client.read.timeout", Integer.class, 10000);
	private static final Observable<String> DEFAULT_UA = Sys.env.getObservableValue(
			"io.basc.framework.http.client.headers.ua", String.class,
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");

	private Integer connectTimeout;

	private Integer readTimeout;

	private String userAgent;

	public HttpClientConfigAccessor() {
	}

	public HttpClientConfigAccessor(HttpClientConfigAccessor configAccessor) {
		this.connectTimeout = configAccessor.connectTimeout;
		this.readTimeout = configAccessor.readTimeout;
		this.userAgent = configAccessor.userAgent;
	}

	public int getConnectTimeout() {
		return connectTimeout == null ? DEFAULT_CONNECT_TIMEOUT.get() : connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout == null ? DEFAULT_READ_TIMEOUT.get() : readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getUserAgent() {
		return userAgent == null ? DEFAULT_UA.get() : userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}
