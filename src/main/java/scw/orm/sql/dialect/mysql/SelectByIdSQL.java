package scw.orm.sql.dialect.mysql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.SimpleGetter;
import scw.orm.sql.SqlORMUtils;
import scw.orm.sql.TableFieldContext;

public final class SelectByIdSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;

	public SelectByIdSQL(MappingOperations mappingOperations, Class<?> clazz, String tableName, Object[] ids)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getName());
		sb.append(tableName);
		sb.append("&");
		sb.append(ids == null ? 0 : ids.length);

		String id = sb.toString();
		this.sql = sqlCache.get(id);
		if (sql == null) {
			synchronized (sqlCache) {
				sql = sqlCache.get(id);
				if (sql == null) {
					sql = getSql(mappingOperations, clazz, tableName, ids);
					sqlCache.put(id, sql);
				}
			}
		}

		int i = 0;
		this.params = new Object[ids == null ? 0 : ids.length];
		TableFieldContext tableFieldContext = SqlORMUtils.getTableFieldContext(mappingOperations, clazz);
		Iterator<MappingContext> iterator = tableFieldContext.getPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			params[i] = mappingOperations.getter(context, new SimpleGetter(ids[i]));
			i++;
		}
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	private String getSql(MappingOperations mappingOperations, Class<?> clazz, String tableName, Object[] ids)
			throws Exception {
		TableFieldContext tableFieldContext = SqlORMUtils.getTableFieldContext(mappingOperations, clazz);
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableName);
		Iterator<MappingContext> iterator = tableFieldContext.getPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			sb.append(UpdateSQL.WHERE);
			for (int i = 0; i < ids.length; i++) {
				if (i != 0) {
					sb.append(AND);
				}

				keywordProcessing(sb, context.getFieldDefinition().getName());
				sb.append("=?");
			}

			if (ids.length == tableFieldContext.getPrimaryKeys().size()) {
				sb.append(" limit 0,1");
			}
		}
		return sb.toString();
	}
}
