package scw.orm.sql.dialect.mysql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import scw.orm.MappingContext;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.TableMappingContext;

public final class SelectInIdSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private static final String IN = " in (";
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;

	public SelectInIdSQL(SqlMapper mappingOperations, Class<?> clazz, String tableName, Object[] ids,
			Collection<?> inIds) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getName());
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
					sql = getSql(mappingOperations, clazz, tableName, ids, inIds);
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

	private String getSql(SqlMapper mappingOperations, Class<?> clazz, String tableName, Object[] ids,
			Collection<?> inIdList) throws Exception {
		TableMappingContext tableFieldContext = mappingOperations.getTableMappingContext(clazz);
		StringBuilder sb = new StringBuilder();
		if (ids.length > 0) {
			Iterator<MappingContext> iterator = tableFieldContext.getPrimaryKeys().iterator();
			while (iterator.hasNext()) {
				if (sb.length() != 0) {
					sb.append(AND);
				}

				MappingContext context = iterator.next();
				keywordProcessing(sb, context.getColumn().getName());
				sb.append("=?");
			}
		}

		if (inIdList != null && !inIdList.isEmpty()) {
			if (sb.length() != 0) {
				sb.append(AND);
			}

			keywordProcessing(sb, tableFieldContext.getPrimaryKeys().get(ids.length).getColumn().getName());
			sb.append(IN);
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
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(WHERE).append(where);
		return sb.toString();
	}
}
