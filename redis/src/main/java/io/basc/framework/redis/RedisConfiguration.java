package io.basc.framework.redis;

import io.basc.framework.json.JSONUtils;

import java.io.Serializable;

public class RedisConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	private String clientName;
	private String host;
	private int port = 6379;
	private String username;
	private String password;
	private int database;
	private int timeout = 5000;

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return JSONUtils.getDefaultJsonSupport().toJSONString(this);
	}
}
