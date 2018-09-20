package shuchaowen.core.db.sql.format.mysql;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.sql.SQL;

public class InsertSQL implements SQL{
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;
	
	public InsertSQL(TableInfo tableInfo, String tableName, Object obj){
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}
		
		this.sql = sqlCache.get(tableName);
		if(sql == null){
			synchronized (sqlCache) {
				sql = sqlCache.get(tableName);
				if(sql == null){
					sql = getSql(tableInfo, tableName, obj);
					sqlCache.put(tableName, sql);
				}
			}
		}
		this.params = getParams(tableInfo, obj);
	}
	
	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
	
	private static String getSql(TableInfo tableInfo, String tableName, Object obj){
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		ColumnInfo columnInfo;
		for (int i = 0; i < tableInfo.getColumns().length; i++) {
			columnInfo = tableInfo.getColumns()[i];
			if (i > 0) {
				cols.append(",");
				values.append(",");
			}

			cols.append(columnInfo.getSqlColumnName());
			values.append("?");
		}
		
		sql.append("insert into `");
		sql.append(tableName);
		sql.append("`(");
		sql.append(cols);
		sql.append(") values(");
		sql.append(values);
		sql.append(")");
		return sql.toString();
	}
	
	private static Object[] getParams(TableInfo tableInfo, Object obj) {
		Object[] params = new Object[tableInfo.getColumns().length];
		int i=0;
		for (; i < tableInfo.getColumns().length; i++) {
			params[i] = tableInfo.getColumns()[i].getValue(obj);
		}
		return params;
	}
}
