package scw.sql.orm.dialect.mysql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import scw.sql.orm.Column;
import scw.sql.orm.ObjectRelationalMapping;

public final class SelectByIdSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private static Map<String, String> sqlCache = new HashMap<String, String>();
	private String sql;
	private Object[] params;

	public SelectByIdSQL(ObjectRelationalMapping objectRelationalMapping, Class<?> clazz, String tableName, Collection<Object> ids){
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
					sql = getSql(objectRelationalMapping, clazz, tableName, ids);
					sqlCache.put(id, sql);
				}
			}
		}

		this.params = new Object[ids.size()];
		if (params.length > 0) {
			Iterator<Column> iterator = objectRelationalMapping.getPrimaryKeys(clazz).iterator();
			Iterator<Object> valueIterator = ids.iterator();
			int i = 0;
			while (iterator.hasNext() && valueIterator.hasNext()) {
				Column column = iterator.next();
				params[i++] = column.toDataBaseValue(valueIterator.next());
			}
		}

	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

	private String getSql(ObjectRelationalMapping objectRelationalMapping, Class<?> clazz, String tableName, Collection<Object> ids) {
		StringBuilder sb = new StringBuilder();
		sb.append(SELECT_ALL_PREFIX);
		keywordProcessing(sb, tableName);
		Collection<Column> primaryKeys = objectRelationalMapping.getPrimaryKeys(clazz);
		Iterator<Column> iterator = primaryKeys.iterator();
		Iterator<Object> valueIterator = ids.iterator();
		if (iterator.hasNext() && valueIterator.hasNext()) {
			sb.append(UpdateSQL.WHERE);
		}

		while (iterator.hasNext() && valueIterator.hasNext()) {
			Column column = iterator.next();
			valueIterator.next();
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			if (iterator.hasNext() && valueIterator.hasNext()) {
				sb.append(AND);
			}
		}

		if (ids.size() == primaryKeys.size()) {
			sb.append(" limit 0,1");
		}
		return sb.toString();
	}
}
