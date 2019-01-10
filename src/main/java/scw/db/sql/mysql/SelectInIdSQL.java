package scw.db.sql.mysql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.database.SQL;
import scw.database.TableInfo;

public class SelectInIdSQL implements SQL {
	private static final long serialVersionUID = 1L;
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;

	public SelectInIdSQL(TableInfo info, String tableName, Object[] ids, Collection<?> inIds) {
		StringBuilder sb = new StringBuilder();
		sb.append(info.getClassInfo().getName());
		sb.append(tableName);
		sb.append("&");
		sb.append(ids.length);
		sb.append("&").append(inIds == null ? 0 : inIds.size());

		String id = sb.toString();
		this.sql = sqlCache.get(id);
		if (sql == null) {
			synchronized (sqlCache) {
				sql = sqlCache.get(id);
				if (sql == null) {
					sql = getSql(info, tableName, ids, inIds);
					sqlCache.put(id, sql);
				}
			}
		}

		if (inIds == null || inIds.size() == 0) {
			this.params = ids;
		} else {
			this.params = new Object[ids.length + inIds.size()];
			System.arraycopy(ids, 0, params, 0, ids.length);
			Object[] arr = inIds.toArray(new Object[inIds.size()]);
			System.arraycopy(arr, 0, params, ids.length, arr.length);
		}
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	private static String getSql(TableInfo info, String tableName, Object[] ids, Collection<?> inIdList) {
		StringBuilder sb = new StringBuilder();
		if (ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				if (sb.length() != 0) {
					sb.append(" and ");
				}
				sb.append(info.getPrimaryKeyColumns()[i].getSQLName(tableName));
				sb.append("=?");
			}
		}

		if (inIdList != null && !inIdList.isEmpty()) {
			if (sb.length() != 0) {
				sb.append(" and ");
			}

			sb.append(info.getPrimaryKeyColumns()[ids.length].getSQLName(tableName));
			sb.append(" in (");
			for (int i = 0; i < inIdList.size(); i++) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append("?");
			}
			sb.append(")");
		}

		String where = sb.toString();
		sb = new StringBuilder();
		sb.append("select * from `").append(tableName).append("`");
		sb.append(" where ").append(where);
		return sb.toString();
	}
}
