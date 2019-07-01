package scw.sql.orm.mysql;

import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;
import scw.sql.orm.enums.CasType;

public class UpdateSQL extends MysqlOrmSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQL(Object obj, TableInfo tableInfo, String tableName)
			throws IllegalArgumentException, IllegalAccessException {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException(tableName + " not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
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

			keywordProcessing(sb, columnInfo.getName());
			if (columnInfo.getCas() != null && columnInfo.getCas().value() == CasType.AUTO) {
				sb.append("=");
				keywordProcessing(sb, columnInfo.getName());
				sb.append("+1");
			} else {
				sb.append("=?");
				params[index++] = ORMUtils.get(columnInfo.getField(), obj);
			}
		}

		sb.append(WHERE);
		for (i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(AND);
			}

			keywordProcessing(sb, columnInfo.getName());
			sb.append("=?");
			params[index++] = ORMUtils.get(columnInfo.getField(), obj);
		}

		for (i = 0; i < tableInfo.getNotPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getNotPrimaryKeyColumns()[i];
			if (columnInfo.getCas() == null) {
				continue;
			}

			sb.append(AND);
			keywordProcessing(sb, columnInfo.getName());
			sb.append("=?");
			params[index++] = ORMUtils.get(columnInfo.getField(), obj);
		}

		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
