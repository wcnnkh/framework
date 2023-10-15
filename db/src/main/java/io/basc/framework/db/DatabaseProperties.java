package io.basc.framework.db;

import lombok.Data;

@Data
public class DatabaseProperties implements Cloneable {
	private String url;
	private String name;
	private String username;
	private String password;
	private String driverClassName;
	/**
	 * 基础包路径
	 */
	private String basePackage;
	/**
	 * 是否自动创建表
	 */
	private boolean automaticallyCreateTables = false;
}
