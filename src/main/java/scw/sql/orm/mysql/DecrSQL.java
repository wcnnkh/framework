package scw.sql.orm.mysql;

import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public class DecrSQL implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	private String decrSql(TableInfo tableInfo, String tableName,
			String fieldName, double limit, Double minValue) {
		StringBuilder sb = new StringBuilder(512);
		sb.append("update ");
		sb.append("`");
		sb.append(tableName);
		sb.append("`");
		sb.append(" set ");

		ColumnInfo incrColumn = tableInfo.getColumnInfo(fieldName);
		sb.append(incrColumn.getSqlColumnName());
		sb.append("=");
		sb.append(incrColumn.getSqlColumnName());
		sb.append("-").append(limit);

		sb.append(" WHERE ");
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
		}

		if (minValue != null) {
			sb.append(" and ");
			sb.append(incrColumn.getSqlColumnName());
			sb.append(">=").append(minValue);
		}
		return sb.toString();
	}

	public DecrSQL(TableInfo tableInfo, String tableName, Object[] primaryKeys,
			String fieldName, double limit, Double minValue) {
		if (tableInfo.getPrimaryKeyColumns().length != primaryKeys.length) {
			throw new NullPointerException("not found primary key");
		}

		this.params = primaryKeys;
		this.sql = decrSql(tableInfo, tableName, fieldName, limit, minValue);
	}

	public DecrSQL(Object obj, TableInfo tableInfo, String tableName,
			String fieldName, double limit, Double minValue) {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		this.params = ORMUtils.getPrimaryKey(obj, tableInfo);
		this.sql = decrSql(tableInfo, tableName, fieldName, limit, minValue);
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	public boolean isStoredProcedure() {
		return false;
	}
}
