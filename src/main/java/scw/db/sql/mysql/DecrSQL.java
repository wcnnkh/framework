package scw.db.sql.mysql;

import scw.database.ColumnInfo;
import scw.database.TableInfo;
import scw.jdbc.Sql;

public class DecrSQL implements Sql{
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;
	
	public DecrSQL(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit, Double minValue) throws IllegalArgumentException, IllegalAccessException{
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}
		
		this.params = new Object[tableInfo.getPrimaryKeyColumns().length + (minValue==null? 1:3)];
		int index = 0;
		
		if(minValue == null){
			params[index++] = limit;
		}else{
			params[index++] = limit;
			params[index++] = minValue;
			params[index++] = limit;
		}
		
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
