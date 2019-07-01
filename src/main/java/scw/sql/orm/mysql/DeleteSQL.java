package scw.sql.orm.mysql;

import scw.core.exception.ParameterException;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public class DeleteSQL extends MysqlOrmSql {
	private static final long serialVersionUID = 1L;
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
			params[i] = ORMUtils.get(columnInfos[i].getField(), obj);
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
		keywordProcessing(sql, tableName);
		sql.append(WHERE);
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sql.append(AND);
			}

			keywordProcessing(sql, columnInfo.getName());
			sql.append("=?");
		}
		return sql.toString();
	}
}
