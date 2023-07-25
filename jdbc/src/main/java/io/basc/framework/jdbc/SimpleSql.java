package io.basc.framework.jdbc;

import java.io.Serializable;

public final class SimpleSql extends AbstractSql implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;
	private boolean storedProcedure;

	protected SimpleSql() {
	};

	public SimpleSql(String sql, Object... params) {
		this.sql = sql;
		this.params = params;
	}

	public SimpleSql(boolean storedProcedure, String sql, Object... params) {
		this.storedProcedure = storedProcedure;
		this.sql = sql;
		this.params = params;
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	public boolean isStoredProcedure() {
		return storedProcedure;
	}

	protected volatile transient String toStringCache;

	@Override
	public String toString() {
		if (toStringCache == null) {
			synchronized (this) {
				if (toStringCache == null) {
					this.toStringCache = super.toString();
				}
			}
		}
		return this.toStringCache;
	}
}
