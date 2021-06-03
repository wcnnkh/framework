package scw.sql.orm.dialect;

import scw.env.Sys;

public class DialectHelper {
	private static final SqlTypeFactory SQL_TYPE_FACTORY = Sys.env.getServiceLoader(SqlTypeFactory.class, "scw.sql.orm.dialect.DefaultSqlTypeFactory").first();
	private static final char POINT = '.';
	private String escapeCharacter = "`";
	private SqlTypeFactory sqlTypeFactory;

	public SqlTypeFactory getSqlTypeFactory() {
		return sqlTypeFactory == null? SQL_TYPE_FACTORY : sqlTypeFactory;
	}

	public void setSqlTypeFactory(SqlTypeFactory sqlTypeFactory) {
		this.sqlTypeFactory = sqlTypeFactory;
	}

	public String getEscapeCharacter() {
		return escapeCharacter;
	}

	public void setEscapeCharacter(String escapeCharacter) {
		this.escapeCharacter = escapeCharacter;
	}

	public void keywordProcessing(StringBuilder sb, String column) {
		sb.append(getEscapeCharacter()).append(column).append(getEscapeCharacter());
	}

	public void keywordProcessing(StringBuilder sb, String tableName, String column) {
		sb.append(getEscapeCharacter()).append(tableName).append(getEscapeCharacter());
		sb.append(POINT);
		sb.append(getEscapeCharacter()).append(column).append(getEscapeCharacter());
	}

	public String getSqlName(String tableName, String column) {
		StringBuilder sb = new StringBuilder();
		keywordProcessing(sb, tableName, column);
		return sb.toString();
	}

	public String getCreateTablePrefix() {
		return "CREATE TABLE IF NOT EXISTS";
	}
}
