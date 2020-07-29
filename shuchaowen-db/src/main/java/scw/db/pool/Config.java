package scw.db.pool;

import scw.core.utils.StringUtils;

public class Config {
	private String url;
	private String user;
	private String password;
	private String dirverClassName;
	private int initialSize;
	private int minimumSize;
	private int maximumSize;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (StringUtils.isEmpty(url)) {
			throw new IllegalArgumentException("The url cannot be null");
		}
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDirverClassName() {
		return dirverClassName;
	}

	public void setDirverClassName(String dirverClassName) {
		if (StringUtils.isEmpty(dirverClassName)) {
			throw new IllegalArgumentException("The dirverClassName cannot be null");
		}
		this.dirverClassName = dirverClassName;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(int maximumSize) {
		this.maximumSize = maximumSize;
	}

	public int getMinimumSize() {
		return minimumSize;
	}

	public void setMinimumSize(int minimumSize) {
		this.minimumSize = minimumSize;
	}
}
