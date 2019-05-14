package scw.sql.orm.mysql;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.core.exception.ParameterException;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public final class InsertSQL implements Sql {
	private static final long serialVersionUID = 1L;
	protected static final String INSERT_INTO_PREFIX = "insert into `";
	protected static final String VALUES = ") values(";
	
	private volatile static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;

	public InsertSQL(TableInfo tableInfo, String tableName, Object obj) {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("not found primary key");
		}

		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getSource().getName());
		sb.append(":");
		sb.append(tableName);
		String id = sb.toString();
		this.sql = sqlCache.get(id);
		if (sql == null) {
			synchronized (sqlCache) {
				sql = sqlCache.get(id);
				if (sql == null) {
					sql = getSql(tableInfo, tableName, obj);
					sqlCache.put(id, sql);
				}
			}
		}
		try {
			this.params = getParams(tableInfo, obj);
		} catch (Exception e) {
			throw new ParameterException(e);
		}
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	private static String getSql(TableInfo tableInfo, String tableName, Object obj) {
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		int index = 0;
		for (ColumnInfo columnInfo : tableInfo.getColumns()) {
			if (columnInfo.getAutoIncrement() != null) {
				continue;
			}

			if (index++ > 0) {
				cols.append(",");
				values.append(",");
			}

			cols.append(columnInfo.getSqlColumnName());
			values.append("?");
		}

		sql.append(INSERT_INTO_PREFIX);
		sql.append(tableName);
		sql.append("`(");
		sql.append(cols);
		sql.append(VALUES);
		sql.append(values);
		sql.append(")");
		return sql.toString();
	}

	private static Object[] getParams(TableInfo tableInfo, Object obj)
			throws IllegalArgumentException, IllegalAccessException {
		LinkedList<Object> list = new LinkedList<Object>();
		for (ColumnInfo columnInfo : tableInfo.getColumns()) {
			if (columnInfo.getAutoIncrement() != null) {
				continue;
			}

			list.add(columnInfo.getValueToDB(obj));
		}
		return list.toArray();
	}

	public boolean isStoredProcedure() {
		return false;
	}
}
