package shuchaowen.db.sql.mysql;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.db.ColumnInfo;
import shuchaowen.db.TableInfo;
import shuchaowen.db.sql.SQL;

public class DeleteSQL implements SQL{
	private static final long serialVersionUID = 1L;
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;	
	
	public DeleteSQL(Object obj, TableInfo tableInfo, String tableName){
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		sb.append(":");
		sb.append(tableName);
		String id = sb.toString();
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
		try {
			this.params = getParams(tableInfo, obj);
		} catch (Exception e){
			throw new ShuChaoWenRuntimeException(e);
		}
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
	
	private static Object[] getParams(TableInfo tableInfo, Object obj) throws IllegalArgumentException, IllegalAccessException {
		Object[] params = new Object[tableInfo.getPrimaryKeyColumns().length];
		int i;
		for (i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			params[i] = tableInfo.getPrimaryKeyColumns()[i].getValueToDB(obj);;
		}
		return params;
	}
}
