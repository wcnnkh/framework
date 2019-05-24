package scw.sql.orm.mysql;

import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public final class UpdateSQL implements Sql {
	private static final long serialVersionUID = 1L;
	protected static final String UPDATE_PREFIX = "update `";
	protected static final String SET = "` set ";
	protected static final String WHERE = " where ";
	protected static final String AND = " and ";
	protected static final String OR = " or ";

	private String sql;
	private Object[] params;

	public UpdateSQL(Object obj, TableInfo tableInfo, String tableName)
			throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException(tableName + " not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		sb.append(tableName);
		sb.append(SET);
		int index = 0;
		int i;
		ColumnInfo columnInfo;
		this.params = new Object[tableInfo.getNotPrimaryKeyColumns().length + tableInfo.getPrimaryKeyColumns().length];
		for (i = 0; i < tableInfo.getNotPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getNotPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(",");
			}

			sb.append("`");
			sb.append(columnInfo.getName());
			sb.append("`=?");
			params[index++] = columnInfo.getValueToDB(obj);
		}

		sb.append(WHERE);
		for (i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(AND);
			}
			
			sb.append("`");
			sb.append(columnInfo.getName());
			sb.append("`=?");
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
