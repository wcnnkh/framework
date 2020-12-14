package scw.sql.orm.dialect.oracle;

import scw.sql.orm.dialect.DialectSql;

public final class LastInsertIdSql extends DialectSql{
	private static final long serialVersionUID = 1L;
	private String sql;

	public LastInsertIdSql(String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("select seq.currval from `").append(tableName).append("`");
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return null;
	}
}
