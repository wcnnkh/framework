package scw.sql.orm.mysql;

import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public class MaxIdSql extends MysqlOrmSql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public MaxIdSql(TableInfo info, String tableName, String idField) {
		StringBuilder sb = new StringBuilder();
		ColumnInfo columnInfo = info.getColumnInfo(idField);
		sb.append("select ");
		keywordProcessing(sb, columnInfo.getName());
		sb.append(" from ");
		keywordProcessing(sb, tableName);
		sb.append(" order by ");
		keywordProcessing(sb, columnInfo.getName());
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
