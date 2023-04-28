package io.basc.framework.data.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class HostAndPort implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;
	private int port;

	public HostAndPort() {
	}

	public HostAndPort(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
