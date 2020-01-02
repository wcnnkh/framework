package scw.orm.sql.dialect.mysql;

import scw.orm.sql.dialect.DialectSql;

public abstract class MysqlDialectSql extends DialectSql {
	private static final long serialVersionUID = 1L;
	private static final char ESCAPE_CHARACTER = '`';
	private static final char POINT = '.';

	@Override
	public void keywordProcessing(StringBuilder sb, String column) {
		sb.append(ESCAPE_CHARACTER).append(column).append(ESCAPE_CHARACTER);
	}

	public void keywordProcessing(StringBuilder sb, String tableName, String column) {
		sb.append(ESCAPE_CHARACTER).append(tableName).append(ESCAPE_CHARACTER);
		sb.append(POINT);
		sb.append(ESCAPE_CHARACTER).append(column).append(ESCAPE_CHARACTER);
	}

	public String getSqlName(String tableName, String column) {
		StringBuilder sb = new StringBuilder();
		keywordProcessing(sb, tableName, column);
		return sb.toString();
	}
}
