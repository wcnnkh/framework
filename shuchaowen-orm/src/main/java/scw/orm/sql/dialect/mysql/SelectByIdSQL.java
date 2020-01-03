package scw.orm.sql.dialect.mysql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import scw.orm.MappingContext;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.SqlMapper;
import scw.orm.support.SimpleGetter;

public final class SelectByIdSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;

	public SelectByIdSQL(SqlMapper mappingOperations, Class<?> clazz, String tableName, Collection<Object> ids){
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getName());
		sb.append(tableName);
		sb.append("&");
		sb.append(ids.size());

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

		this.params = new Object[ids.size()];
		if (params.length > 0) {
			ObjectRelationalMapping tableFieldContext = mappingOperations.getObjectRelationalMapping(clazz);
			Iterator<MappingContext> iterator = tableFieldContext.getPrimaryKeys().iterator();
			Iterator<Object> valueIterator = ids.iterator();
			int i = 0;
			while (iterator.hasNext() && valueIterator.hasNext()) {
				MappingContext context = iterator.next();
				params[i++] = mappingOperations.getter(context, new SimpleGetter(valueIterator.next()));
			}
		}

	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	private String getSql(SqlMapper mappingOperations, Class<?> clazz, String tableName, Collection<Object> ids) {
		ObjectRelationalMapping tableFieldContext = mappingOperations.getObjectRelationalMapping(clazz);
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableName);
		Iterator<MappingContext> iterator = tableFieldContext.getPrimaryKeys().iterator();
		Iterator<Object> valueIterator = ids.iterator();
		if (iterator.hasNext() && valueIterator.hasNext()) {
			sb.append(UpdateSQL.WHERE);
		}

		while (iterator.hasNext() && valueIterator.hasNext()) {
			MappingContext context = iterator.next();
			valueIterator.next();
			keywordProcessing(sb, context.getColumn().getName());
			sb.append("=?");
			if (iterator.hasNext() && valueIterator.hasNext()) {
				sb.append(AND);
			}
		}

		if (ids.size() == tableFieldContext.getPrimaryKeys().size()) {
			sb.append(" limit 0,1");
		}
		return sb.toString();
	}
}
