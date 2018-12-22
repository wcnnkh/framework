package scw.db.sql.mysql;

import scw.database.ColumnInfo;
import scw.database.SQL;
import scw.database.TableInfo;

public class CreateTableSQL implements SQL{
	private static final long serialVersionUID = 1L;
	private String sql;
	
	public CreateTableSQL(TableInfo tableInfo, String tableName){
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("`");
		sb.append(" (");
		int count = 0;

		for (ColumnInfo columnInfo : tableInfo.getColumns()) {
			if (count != 0) {
				sb.append(",");
			}
			count++;

			int len = columnInfo.getLength();
			sb.append(columnInfo.getSqlColumnName());
			sb.append(" ");
			if ("java.lang.String".equals(columnInfo.getTypeName())) {
				sb.append("varchar");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(255)");
				}
			} else if ("float".equals(columnInfo.getTypeName()) || "java.lang.Float".equals(columnInfo.getTypeName())) {
				sb.append("float");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(10)");
				}
			} else if ("double".equals(columnInfo.getTypeName()) || "java.lang.Double".equals(columnInfo.getTypeName())) {
				sb.append("double");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(20)");
				}
			} else if ("long".equals(columnInfo.getTypeName()) || "java.lang.Long".equals(columnInfo.getTypeName())) {
				sb.append("bigint");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(20)");
				}
			} else if ("int".equals(columnInfo.getTypeName()) || "java.lang.Integer".equals(columnInfo.getTypeName())) {
				sb.append("int");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(10)");
				}
			} else if ("short".equals(columnInfo.getTypeName()) || "java.lang.Short".equals(columnInfo.getTypeName())) {
				sb.append("SMALLINT");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(5)");
				}
			} else if ("byte".equals(columnInfo.getTypeName()) || "java.lang.Byte".equals(columnInfo.getTypeName())) {
				sb.append("bit");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(1)");
				}
			} else if ("boolean".equals(columnInfo.getTypeName()) || "java.lang.Boolean".equals(columnInfo.getTypeName())) {
				sb.append("bit(1)");
			} else {
				sb.append(columnInfo.getTypeName());
				if (len > 0) {
					sb.append("(").append(len).append(")");
				}
			}
			sb.append(" ");

			if (!columnInfo.isNullAble()) {
				sb.append("not null ");
			}
		}

		if (tableInfo.getPrimaryKeyColumns().length > 0) {
			if (count > 0) {
				sb.append(",");
			}

			count = 0;
			sb.append("primary key (");
			for (ColumnInfo columnInfo : tableInfo.getPrimaryKeyColumns()) {
				if (count > 0) {
					sb.append(",");
				}
				count++;
				sb.append(columnInfo.getSqlColumnName());
			}
			sb.append(")");
		}

		sb.append(")");
		sb.append(" ENGINE=").append(tableInfo.getEngine());
		sb.append(" DEFAULT");
		sb.append(" CHARSET=").append(tableInfo.getCharset());
		sb.append(" ROW_FORMAT=").append(tableInfo.getRow_format());
		this.sql = sb.toString();
	}
	
	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return null;
	}
}
