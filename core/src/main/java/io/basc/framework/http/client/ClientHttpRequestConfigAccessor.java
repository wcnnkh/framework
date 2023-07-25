package io.basc.framework.http.client;

import io.basc.framework.env.Sys;
import io.basc.framework.event.observe.Observable;

public class ClientHttpRequestConfigAccessor {
	private static final Observable<Integer> DEFAULT_CONNECT_TIMEOUT = Sys.getEnv().getProperties()
			.getObservable("http.client.connect.timeout").map((e) -> e.or(10000).getAsInt());
	private static final Observable<Integer> DEFAULT_READ_TIMEOUT = Sys.getEnv().getProperties()
			.getObservable("http.client.read.timeout").map((e) -> e.or(10000).getAsInt());

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
