package io.basc.framework.sql;


public class SimpleSql extends SerializableSql{
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
}
