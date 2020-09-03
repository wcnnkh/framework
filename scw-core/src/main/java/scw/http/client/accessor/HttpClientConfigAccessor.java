package scw.http.client.accessor;

import scw.core.GlobalPropertyFactory;
import scw.event.support.DynamicValue;

public class HttpClientConfigAccessor {
	private static final DynamicValue<Integer> DEFAULT_CONNECT_TIMEOUT = GlobalPropertyFactory.getInstance()
			.getDynamicValue("scw.http.client.connect.timeout", Integer.class, 10000);
	private static final DynamicValue<Integer> DEFAULT_READ_TIMEOUT = GlobalPropertyFactory.getInstance()
			.getDynamicValue("scw.http.client.read.timeout", Integer.class, 10000);
	private static final DynamicValue<String> DEFAULT_UA = GlobalPropertyFactory.getInstance().getDynamicValue(
			"scw.http.client.headers.ua", String.class,
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");

	private Integer connectTimeout;

	private Integer readTimeout;

	private String userAgent;

	public void setConfig(HttpClientConfigAccessor config) {
		this.connectTimeout = config.connectTimeout;
		this.readTimeout = config.readTimeout;
		this.userAgent = config.userAgent;
	}

	public int getConnectTimeout() {
		return connectTimeout == null ? DEFAULT_CONNECT_TIMEOUT.getValue() : connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout == null ? DEFAULT_READ_TIMEOUT.getValue() : readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getUserAgent() {
		return userAgent == null ? DEFAULT_UA.getValue() : userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}
