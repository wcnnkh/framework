package io.basc.framework.redis;

import java.io.Serializable;

import lombok.Data;

@Data
public class RedisConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	private String clientName;
	private String host;
	private int port = 6379;
	private String username;
	private String password;
	private int database;
	private int timeout = 5000;
}
