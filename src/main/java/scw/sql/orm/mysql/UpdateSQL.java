package scw.sql.orm.mysql;

import java.util.ArrayList;
import java.util.List;

import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;
import scw.sql.orm.enums.CasType;

public class UpdateSQL extends MysqlOrmSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQL(Object obj, TableInfo tableInfo, String tableName)
			throws Exception {
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException(tableName + " not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(SET);
		int i;
		ColumnInfo columnInfo;
		List<Object> params = new ArrayList<Object>(tableInfo.getColumns().length);
		for (i = 0; i < tableInfo.getNotPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getNotPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(",");
			}

			keywordProcessing(sb, columnInfo.getName());
			if (columnInfo.getCasType() == CasType.AUTO_INCREMENT) {
				sb.append("=");
				keywordProcessing(sb, columnInfo.getName());
				sb.append("+1");
			} else {
				sb.append("=?");
				params.add(columnInfo.get(obj));
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
			params.add(columnInfo.get(obj));
		}

		for (i = 0; i < tableInfo.getNotPrimaryKeyColumns().length; i++) {
			columnInfo = tableInfo.getNotPrimaryKeyColumns()[i];
			if (columnInfo.getCasType() == CasType.NOTHING) {
				continue;
			}

			sb.append(AND);
			keywordProcessing(sb, columnInfo.getName());
			sb.append("=?");
			params.add(columnInfo.get(obj));
		}

		this.sql = sb.toString();
		this.params = params.toArray(new Object[params.size()]);
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
