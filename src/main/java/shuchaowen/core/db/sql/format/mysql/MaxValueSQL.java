package shuchaowen.core.db.sql.format.mysql;

import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.sql.SQL;

public class MaxValueSQL implements SQL{
	private String sql;
	public MaxValueSQL(TableInfo tableInfo, String tableName, String fieldName){
		ColumnInfo columnName = tableInfo.getColumnInfo(fieldName);
		StringBuilder sb = new StringBuilder();
		sb.append("select max(").append(columnName.getSQLName(tableName));
		sb.append(") from `");
		sb.append(tableName);
		sb.append("`");
		this.sql = sb.toString();
	}
	
	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return null;
	}
	
}
