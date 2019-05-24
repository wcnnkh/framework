package scw.sql.orm.mysql;

import scw.core.exception.ParameterException;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public final class DeleteSQL implements Sql {
	private static final long serialVersionUID = 1L;
	protected static final String DELETE_PREFIX = "delete from `";
	protected static final String WHERE = "` where ";
	private String sql;
	private Object[] params;

	public DeleteSQL(TableInfo tableInfo, String tableName, Object[] parimayKeys) {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (tableInfo.getPrimaryKeyColumns().length != parimayKeys.length) {
			throw new ParameterException("主键数量不一致:" + tableName);
		}

		this.params = parimayKeys;
		this.sql = formatSql(tableInfo, tableName);
	}

	public DeleteSQL(Object obj, TableInfo tableInfo, String tableName)
			throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		ColumnInfo[] columnInfos = tableInfo.getPrimaryKeyColumns();
		this.params = new Object[columnInfos.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = columnInfos[i].getValueToDB(obj);
		}
		this.sql = formatSql(tableInfo, tableName);
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	private String formatSql(TableInfo tableInfo, String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		sql.append(tableName);
		sql.append(WHERE);
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sql.append(UpdateSQL.AND);
			}

			sql.append("`");
			sql.append(columnInfo.getName());
			sql.append("`=?");
		}
		return sql.toString();
	}

	public boolean isStoredProcedure() {
		return false;
	}
}
