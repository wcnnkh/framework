package scw.sql.orm;

public class ColumnDefaultConfig {
	private final String sqlType;
	private final int len;

	public ColumnDefaultConfig(String sqlType, int len) {
		this.sqlType = sqlType;
		this.len = len;
	}

	public String getSqlType() {
		return sqlType;
	}

	public int getLen() {
		return len;
	}
}
