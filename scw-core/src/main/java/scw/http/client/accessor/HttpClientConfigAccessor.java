package scw.http.client.accessor;

import scw.env.SystemEnvironment;
import scw.event.Observable;

public class HttpClientConfigAccessor {
	private static final Observable<Integer> DEFAULT_CONNECT_TIMEOUT = SystemEnvironment.getInstance()
			.getObservableValue("scw.http.client.connect.timeout", Integer.class, 10000);
	private static final Observable<Integer> DEFAULT_READ_TIMEOUT = SystemEnvironment.getInstance()
			.getObservableValue("scw.http.client.read.timeout", Integer.class, 10000);
	private static final Observable<String> DEFAULT_UA = SystemEnvironment.getInstance().getObservableValue(
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
