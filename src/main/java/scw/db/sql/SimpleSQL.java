package scw.db.sql;

import scw.database.SQL;

public class SimpleSQL implements SQL {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	protected SimpleSQL() {
	};

	public SimpleSQL(String sql, Object... params) {
		this.sql = sql;
		this.params = params;
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
