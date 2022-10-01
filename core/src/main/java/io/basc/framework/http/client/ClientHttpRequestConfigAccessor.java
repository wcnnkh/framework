package io.basc.framework.http.client;

import io.basc.framework.env.Sys;
import io.basc.framework.event.Observable;

public class ClientHttpRequestConfigAccessor {
	private static final Observable<Integer> DEFAULT_CONNECT_TIMEOUT = Sys.getEnv().getProperties()
			.getObservableValue("http.client.connect.timeout", Integer.class, 10000);
	private static final Observable<Integer> DEFAULT_READ_TIMEOUT = Sys.getEnv().getProperties()
			.getObservableValue("http.client.read.timeout", Integer.class, 10000);

	private Integer connectTimeout;

	private Integer readTimeout;

	public ClientHttpRequestConfigAccessor() {
	}

	public ClientHttpRequestConfigAccessor(ClientHttpRequestConfigAccessor configAccessor) {
		this.connectTimeout = configAccessor.connectTimeout;
		this.readTimeout = configAccessor.readTimeout;
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
}
