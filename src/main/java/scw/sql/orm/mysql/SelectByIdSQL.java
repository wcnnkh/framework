package scw.sql.orm.mysql;

import java.util.HashMap;
import java.util.Map;

import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;

public final class SelectByIdSQL extends MysqlOrmSql {
	private static final long serialVersionUID = 1L;
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;

	public SelectByIdSQL(TableInfo info, String tableName, Object[] ids) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(info.getSource().getName());
		sb.append(tableName);
		sb.append("&");
		sb.append(ids == null ? 0 : ids.length);

		String id = sb.toString();
		this.sql = sqlCache.get(id);
		if (sql == null) {
			synchronized (sqlCache) {
				sql = sqlCache.get(id);
				if (sql == null) {
					sql = getSql(info, tableName, ids);
					sqlCache.put(id, sql);
				}
			}
		}

		ColumnInfo[] columnInfos = info.getPrimaryKeyColumns();
		this.params = new Object[ids == null ? 0 : ids.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = columnInfos[i].toSqlField(ids[i]);
		}
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	private String getSql(TableInfo info, String tableName, Object[] ids) {
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableName);
		if (ids != null && ids.length > 0) {

			sb.append(UpdateSQL.WHERE);
			for (int i = 0; i < ids.length; i++) {
				if (i != 0) {
					sb.append(AND);
				}

				keywordProcessing(sb, info.getPrimaryKeyColumns()[i].getName());
				sb.append("=?");
			}

			if (ids.length == info.getPrimaryKeyColumns().length) {
				sb.append(" limit 0,1");
			}
		}
		return sb.toString();
	}
}
