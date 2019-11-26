package scw.orm.sql.dialect.mysql;

public class MaxIdSql extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public MaxIdSql(String tableName, String idField) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		keywordProcessing(sb, idField);
		sb.append(" from ");
		keywordProcessing(sb, tableName);
		sb.append(" order by ");
		keywordProcessing(sb, idField);
		sb.append(" desc");
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return null;
	}
}
