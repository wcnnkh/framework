package scw.sql.orm.mysql;

import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public class MaxIdSql implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public MaxIdSql(TableInfo info, String tableName, String idField) {
		StringBuilder sb = new StringBuilder();
		ColumnInfo columnInfo = info.getColumnInfo(idField);
		sb.append("select ");
		sb.append("`");
		sb.append(columnInfo.getName());
		sb.append("`");
		sb.append(" from ").append("`").append(tableName).append("`");
		sb.append(" order by ");
		sb.append("`");
		sb.append(columnInfo.getName());
		sb.append("`");
		sb.append(" desc");
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
