package scw.sql.orm.mysql;

import scw.core.exception.ParameterException;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

/**
 * 自增
 * 
 * @author shuchaowen
 *
 */
public class DecrByIdSQL extends MysqlOrmSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	private String decrSql(TableInfo tableInfo, String tableName, String fieldName, double limit, Double minValue) {
		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(SET);

		ColumnInfo incrColumn = tableInfo.getColumnInfo(fieldName);
		keywordProcessing(sb, incrColumn.getName());
		sb.append("=");
		keywordProcessing(sb, incrColumn.getName());
		sb.append("-").append(limit);

		sb.append(UpdateSQL.WHERE);
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[i];
			if (i > 0) {
				sb.append(AND);
			}

			keywordProcessing(sb, columnInfo.getName());
			sb.append("=?");
		}

		if (minValue != null) {
			sb.append(AND);
			keywordProcessing(sb, incrColumn.getName());
			sb.append("-").append(limit);
			sb.append(">=").append(minValue);
		}
		return sb.toString();
	}

	public DecrByIdSQL(TableInfo tableInfo, String tableName, Object[] parimayKeys, String fieldName, double limit,
			Double minValue) {
		if (tableInfo.getPrimaryKeyColumns().length != parimayKeys.length) {
			throw new ParameterException("primary key length error");
		}

		this.params = parimayKeys;
		this.sql = decrSql(tableInfo, tableName, fieldName, limit, minValue);
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
