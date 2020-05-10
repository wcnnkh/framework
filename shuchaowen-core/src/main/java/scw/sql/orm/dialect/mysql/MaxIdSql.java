package scw.sql.orm.dialect.mysql;

import scw.sql.orm.Column;
import scw.sql.orm.ObjectRelationalMapping;

public class MaxIdSql extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public MaxIdSql(ObjectRelationalMapping objectRelationalMapping, Class<?> clazz, String tableName, String idField) {
		Column column = objectRelationalMapping.getColumn(clazz, idField);
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		keywordProcessing(sb, column.getName());
		sb.append(" from ");
		keywordProcessing(sb, tableName);
		sb.append(" order by ");
		keywordProcessing(sb, column.getName());
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
