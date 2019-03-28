package scw.sql.orm.mysql;

import scw.common.exception.ParameterException;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

/**
 * 自增
 * 
 * @author shuchaowen
 *
 */
public class IncrSQL implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	private String incrSql(TableInfo tableInfo, String tableName,
			String fieldName, double limit, Double maxValue) {
		StringBuilder sb = new StringBuilder(512);
		sb.append("update ");
		sb.append("`");
		sb.append(tableName);
		sb.append("`");
		sb.append(" set ");

		ColumnInfo incrColumn = tableInfo.getColumnInfo(fieldName);
		sb.append(incrColumn.getSqlColumnName());
		sb.append("=");
		sb.append(incrColumn.getSqlColumnName());
		sb.append("+").append(limit);

		sb.append(" WHERE ");
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
		}

		if (maxValue != null) {
			sb.append(" and ");
			sb.append(incrColumn.getSqlColumnName());
			sb.append("<=").append(maxValue);
		}
		return sb.toString();
	}

	public IncrSQL(TableInfo tableInfo, String tableName, Object[] parimayKeys,
			String fieldName, double limit, Double maxValue) {
		if (tableInfo.getPrimaryKeyColumns().length != parimayKeys.length) {
			throw new ParameterException("primary key length error");
		}

		this.params = parimayKeys;
		this.sql = incrSql(tableInfo, tableName, fieldName, limit, maxValue);
	}

	public IncrSQL(Object obj, TableInfo tableInfo, String tableName,
			String fieldName, double limit, Double maxValue) {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		this.params = ORMUtils.getPrimaryKey(obj, tableInfo);
		this.sql = incrSql(tableInfo, tableName, fieldName, limit, maxValue);
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
