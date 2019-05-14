package scw.sql.orm.mysql;

import scw.core.exception.ParameterException;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

/**
 * 自增
 * 
 * @author shuchaowen
 *
 */
public final class IncrByIdSQL implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	private String incrSql(TableInfo tableInfo, String tableName, String fieldName, double limit, Double maxValue) {
		StringBuilder sb = new StringBuilder(512);
		sb.append(UpdateSQL.UPDATE_PREFIX);
		sb.append(tableName);
		sb.append(UpdateSQL.SET);

		ColumnInfo incrColumn = tableInfo.getColumnInfo(fieldName);
		sb.append(incrColumn.getSqlColumnName());
		sb.append("=");
		sb.append(incrColumn.getSqlColumnName());
		sb.append("+").append(limit);

		sb.append(UpdateSQL.WHERE);
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(UpdateSQL.AND);
			}

			sb.append(columnInfo.getSqlColumnName());
			sb.append("=?");
		}

		if (maxValue != null) {
			sb.append(UpdateSQL.AND);
			sb.append(incrColumn.getSqlColumnName());
			sb.append("+").append(limit);
			sb.append("<=").append(maxValue);
		}
		return sb.toString();
	}

	public IncrByIdSQL(TableInfo tableInfo, String tableName, Object[] parimayKeys, String fieldName, double limit,
			Double maxValue) {
		if (tableInfo.getPrimaryKeyColumns().length != parimayKeys.length) {
			throw new ParameterException("primary key length error");
		}

		this.params = parimayKeys;
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
