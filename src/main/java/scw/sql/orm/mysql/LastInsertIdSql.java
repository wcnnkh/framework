package scw.sql.orm.mysql;

import scw.sql.Sql;

public final class LastInsertIdSql implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public LastInsertIdSql(String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("select last_insert_id()");
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return null;
	}

	public boolean isStoredProcedure() {
		return false;
	}

}
