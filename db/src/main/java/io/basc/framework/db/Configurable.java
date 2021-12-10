package io.basc.framework.db;

import io.basc.framework.beans.annotation.ConfigurationProperties;

import java.io.Serializable;

@ConfigurationProperties(prefix = "db")
public class Configurable implements Serializable {
	private static final long serialVersionUID = 1L;
	private String driverClassName;
	private String username;
	private String password;
	private String url;

	// 连接池配置
	private Integer minSize;
	private Integer maxSize;

	/**
	 * 自动创建那些表
	 */
	private String autoCreateTables;
	/**
	 * 自动创建的表是否接受DBManager管理
	 */
	private boolean registerManager;

	/**
	 * 是否自动创建数据库
	 */
	private boolean autoCreateDataBase;

	/**
	 * 方言
	 */
	private String sqlDialect;

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getMinSize() {
		return minSize;
	}

	public void setMinSize(Integer minSize) {
		this.minSize = minSize;
	}

	public Integer getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}

	public String getAutoCreateTables() {
		return autoCreateTables;
	}

	public void setAutoCreateTables(String autoCreateTables) {
		this.autoCreateTables = autoCreateTables;
	}

	public boolean isRegisterManager() {
		return registerManager;
	}

	public void setRegisterManager(boolean registerManager) {
		this.registerManager = registerManager;
	}

	public boolean isAutoCreateDataBase() {
		return autoCreateDataBase;
	}

	public void setAutoCreateDataBase(boolean autoCreateDataBase) {
		this.autoCreateDataBase = autoCreateDataBase;
	}

	public String getSqlDialect() {
		return sqlDialect;
	}

	public void setSqlDialect(String sqlDialect) {
		this.sqlDialect = sqlDialect;
	}
}
