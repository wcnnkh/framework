package shuchaowen.core.db.sql.format.mysql;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class DecrSQL implements SQL{
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;
	
	public DecrSQL(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit, Double minValue){
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		String id = getCacheId(tableName, fieldName, minValue);
		this.sql = sqlCache.get(id);
		if (sql == null) {
			synchronized (sqlCache) {
				sql = sqlCache.get(id);
				if (sql == null) {
					sql = getSql(obj, tableInfo, tableName, fieldName, limit, minValue);
					sqlCache.put(id, sql);
				}
			}
		}
		
		try {
			this.params = getParams(tableInfo, obj, limit, minValue);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
	
	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	private static String getCacheId(String tableName, String fieldName, Double minValue) {
		StringBuilder sb = new StringBuilder();
		sb.append(tableName);
		sb.append(":");
		sb.append(fieldName);
		sb.append(":");
		sb.append(minValue==null);
		return sb.toString();
	}

	private static String getSql(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit, Double minValue) {
		StringBuilder sb = new StringBuilder(512);
		ColumnInfo columnInfo;
		sb.append("update ");
		sb.append("`");
		sb.append(tableName);
		sb.append("`");
		sb.append(" set ");

		columnInfo = tableInfo.getColumnInfo(fieldName);
		sb.append(columnInfo.getSqlColumnName());
		sb.append("=");
		if(minValue == null){//不设置最小值
			sb.append(columnInfo.getSqlColumnName());
			sb.append("-?");
		}else{
			sb.append("IF(");
			sb.append(columnInfo.getSqlColumnName()).append("-?").append("<?");
			sb.append(",null,");
			sb.append(columnInfo.getSqlColumnName()).append("-?)");
		}
		
		sb.append(" WHERE ");
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
		}
		return sb.toString();
	}

	private static Object[] getParams(TableInfo tableInfo, Object obj, double limit, Double minValue) throws IllegalArgumentException, IllegalAccessException {
		Object[] params = new Object[tableInfo.getPrimaryKeyColumns().length + (minValue==null? 1:3)];
		int index = 0;
		
		if(minValue == null){
			params[index++] = limit;
		}else{
			params[index++] = limit;
			params[index++] = minValue;
			params[index++] = limit;
		}
		
		for (ColumnInfo columnInfo : tableInfo.getPrimaryKeyColumns()) {
			params[index++] = columnInfo.getValueToDB(obj);
		}
		return params;
	}
}
