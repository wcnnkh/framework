package scw.sql.orm.mysql;

import java.util.Map;
import java.util.Map.Entry;

import scw.common.Logger;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public class UpdateSQL implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQL(TableInfo tableInfo, String tableName, Map<String, Object> valueMap, Object[] params) {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (valueMap == null || valueMap.isEmpty()) {
			throw new ShuChaoWenRuntimeException("valueMap isEmpty");
		}

		if (params.length == 0) {
			Logger.warn(this.getClass().getName(), "你正在试图更新一个表的所有数据：" + tableName);
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append("update ");
		sb.append("`");
		sb.append(tableName);
		sb.append("`");
		sb.append(" set ");
		int index = 0;
		ColumnInfo columnInfo;
		this.params = new Object[valueMap.size() + params.length];
		for (Entry<String, Object> entry : valueMap.entrySet()) {
			columnInfo = tableInfo.getColumnInfo(entry.getKey());
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			this.params[index++] = entry.getValue();
		}

		sb.append(" where ");
		for (int i = 0; i < params.length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(" and ");
			}
			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
			this.params[index++] = params[i];
		}
		this.sql = sb.toString();
	}

	public UpdateSQL(Object obj, TableInfo tableInfo, String tableName)
			throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException(tableName + " not found primary key");
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

	public boolean isStoredProcedure() {
		return false;
	}
}
