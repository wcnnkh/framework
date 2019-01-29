package scw.db.sql.mysql;

import scw.database.ColumnInfo;
import scw.database.SQL;
import scw.database.TableInfo;

public class SaveOrUpdateSQL implements SQL {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public SaveOrUpdateSQL(Object obj, TableInfo tableInfo, String tableName) throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		this.params = new Object[tableInfo.getColumns().length + tableInfo.getPrimaryKeyColumns().length
				+ tableInfo.getNotPrimaryKeyColumns().length];
		int index = 0;
		StringBuilder sb = new StringBuilder(512);
		ColumnInfo columnInfo;
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		int i;
		for (i = 0; i < tableInfo.getColumns().length; i++) {
			columnInfo = tableInfo.getColumns()[i];
			if (i > 0) {
				cols.append(",");
				values.append(",");
			}

			cols.append(columnInfo.getSqlColumnName());
			values.append("?");
			params[index++] = columnInfo.getValueToDB(obj);
		}

		sb.append("insert into `");
		sb.append(tableName);
		sb.append("`(");
		sb.append(cols);
		sb.append(") values(");
		sb.append(values);
		sb.append(") ON DUPLICATE KEY UPDATE ");

		for (i = 0; i < tableInfo.getColumns().length; i++) {
			columnInfo = tableInfo.getColumns()[i];
			if (i > 0) {
				sb.append(",");
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

	public boolean isStoredProcedure() {
		return false;
	}
}
