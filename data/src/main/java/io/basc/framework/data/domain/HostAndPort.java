package io.basc.framework.data.domain;

import java.io.Serializable;

import io.basc.framework.core.reflect.ReflectionUtils;

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

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
