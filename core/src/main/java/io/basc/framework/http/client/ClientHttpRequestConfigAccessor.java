package io.basc.framework.http.client;

import lombok.Data;

@Data
public class ClientHttpRequestConfigAccessor {
	private int connectTimeout;

	private int readTimeout;

	public ClientHttpRequestConfigAccessor() {
		this.connectTimeout = Integer.getInteger("io.basc.framework.http.client.connect.timeout", 10000);
		this.readTimeout = Integer.getInteger("io.basc.framework.http.client.read.timeout", 10000);
	}

	public ClientHttpRequestConfigAccessor(ClientHttpRequestConfigAccessor configAccessor) {
		this.connectTimeout = configAccessor.connectTimeout;
		this.readTimeout = configAccessor.readTimeout;
	}
}
