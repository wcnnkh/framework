package scw.sql.orm.mysql;

import java.util.LinkedList;

import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public class SaveOrIncrSql implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public SaveOrIncrSql(Object obj, TableInfo tableInfo, String tableName, String fieldName,
			Double maxValue) throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		LinkedList<Object> list = new LinkedList<Object>();
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
			list.add(columnInfo.getValueToDB(obj));
		}

		sb.append("insert into `");
		sb.append(tableName);
		sb.append("`(");
		sb.append(cols);
		sb.append(") values(");
		sb.append(values);
		sb.append(") ON DUPLICATE KEY UPDATE ");

		ColumnInfo incrColumn = tableInfo.getColumnInfo(fieldName);
		sb.append(incrColumn.getSqlColumnName());
		sb.append("=").append(incrColumn.getSqlColumnName()).append("-").append(incrColumn.getValueToDB(obj));
		if (maxValue != null) {
			sb.append(" where ").append(incrColumn.getSqlColumnName()).append(">=").append(maxValue);
		}

		this.sql = sb.toString();
		this.params = list.toArray();
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