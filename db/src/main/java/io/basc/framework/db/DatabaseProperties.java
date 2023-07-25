package io.basc.framework.db;

import lombok.Data;

@Data
public class DatabaseProperties implements Cloneable {
	private String url;
	private String name;
	private String username;
	private String password;
	private String driverClassName;
}
