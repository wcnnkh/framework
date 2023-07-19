package io.basc.framework.sql.config;

import lombok.Data;

@Data
public class ConnectionFactoryProperties {
	private String driverClassName;
	private String url;
	private String username;
	private String password;
}
