package scw.db.sql.mysql;

import scw.database.ColumnInfo;
import scw.database.SQL;
import scw.database.TableInfo;

public class UpdateSQL implements SQL {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQL(Object obj, TableInfo tableInfo, String tableName) throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append("update ");
		sb.append("`");
		sb.append(tableName);
		sb.append("`");
		sb.append(" set ");
		int index = 0;
		int i;
		ColumnInfo columnInfo;
		this.params = new Object[tableInfo.getNotPrimaryKeyColumns().length + tableInfo.getPrimaryKeyColumns().length];
		for (i = 0; i < tableInfo.getNotPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getNotPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(",");
			}

			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			params[index++] = columnInfo.getValueToDB(obj);
		}

		sb.append(" where ");
		for (i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(" and ");
			}
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			params[index++] = columnInfo.getValueToDB(obj);
		}
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
