package shuchaowen.core.db.sql.format.mysql;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.sql.SQL;

public class DeleteSQL implements SQL{
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;	
	
	public DeleteSQL(Object obj, TableInfo tableInfo, String tableName){
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}
		
		String id = tableName;
		this.sql = sqlCache.get(id);
		if(sql == null){
			synchronized (sqlCache) {
				sql = sqlCache.get(id);
				if(sql == null){
					sql = getSql(obj, tableInfo, tableName);
					sqlCache.put(id, sql);
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
	
	private static String getSql(Object obj, TableInfo tableInfo, String tableName){
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append("`");
		sql.append(tableName);
		sql.append("`");
		sql.append(" where ");
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sql.append(" and ");
			}

			sql.append(columnInfo.getSqlColumnName());
			sql.append("=?");
		}
		return sql.toString();
	}
	
	private static Object[] getParams(TableInfo tableInfo, Object obj) {
		Object[] params = new Object[tableInfo.getPrimaryKeyColumns().length];
		int i;
		for (i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			params[i] = tableInfo.getPrimaryKeyColumns()[i].getValue(obj);;
		}
		return params;
	}
}
