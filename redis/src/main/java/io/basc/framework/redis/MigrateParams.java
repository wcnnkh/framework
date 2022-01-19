package io.basc.framework.redis;

public class MigrateParams extends RedisAuth {
	private static final long serialVersionUID = 1L;
	private boolean copy;
	private boolean replace;

	public MigrateParams() {
	}

	public MigrateParams(boolean copy, boolean replace) {
		this.copy = copy;
		this.replace = replace;
	}

	public MigrateParams(boolean copy, boolean replace, String username, String password) {
		super(username, password);
		this.copy = copy;
		this.replace = replace;
	}

	public boolean isCopy() {
		return copy;
	}

	public void setCopy(boolean copy) {
		this.copy = copy;
	}

	public boolean isReplace() {
		return replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}
}
