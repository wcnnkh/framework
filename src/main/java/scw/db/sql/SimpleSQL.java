package scw.db.sql;

import scw.jdbc.Sql;

public class SimpleSQL implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;
	private boolean storedProcedure;

	protected SimpleSQL() {
	};

	public SimpleSQL(String sql, Object... params) {
		this.sql = sql;
		this.params = params;
	}

	public SimpleSQL(boolean storedProcedure, String sql, Object... params) {
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
