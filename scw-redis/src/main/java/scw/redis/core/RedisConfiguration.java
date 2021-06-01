package scw.redis.core;

import java.io.Serializable;

public class RedisConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	private String host;
	private Integer port;
	private String username;
	private String password;
	private Integer database;

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Integer getDatabase() {
		return database;
	}

	public RedisConfiguration setHost(String host) {
		this.host = host;
		return this;
	}

	public RedisConfiguration setPort(Integer port) {
		this.port = port;
		return this;
	}

	public RedisConfiguration setUsername(String username) {
		this.username = username;
		return this;
	}

	public RedisConfiguration setPassword(String password) {
		this.password = password;
		return this;
	}

	public RedisConfiguration setDatabase(Integer database) {
		this.database = database;
		return this;
	}
}
