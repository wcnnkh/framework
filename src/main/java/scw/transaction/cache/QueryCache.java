package scw.transaction.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.sql.ResultSetMapper;
import scw.sql.RowMapper;
import scw.sql.Sql;
import scw.sql.SqlOperations;
import scw.sql.SqlUtils;

final class QueryCache {
	private Map<String, Object> cache;
	private Map<String, Object> listCache;
	private final SqlOperations sqlOperations;

	public QueryCache(SqlOperations sqlOperations) {
		this.sqlOperations = sqlOperations;
	}

	@SuppressWarnings("unchecked")
	public <T> T query(Sql sql, ResultSetMapper<T> resultSetMapper) {
		String id = SqlUtils.getSqlId(sql);
		if (cache == null) {
			cache = new HashMap<String, Object>(8, 1);
		} else if (cache.containsKey(id)) {
			return (T) cache.get(id);
		}

		T v = sqlOperations.query(sql, resultSetMapper);
		cache.put(id, v);
		return v;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> query(Sql sql, RowMapper<T> rowMapper) {
		String id = SqlUtils.getSqlId(sql);
		if (listCache == null) {
			listCache = new HashMap<String, Object>(8, 1);
		} else if (listCache.containsKey(id)) {
			return (List<T>) listCache.get(id);
		}

		List<T> v = sqlOperations.query(sql, rowMapper);
		listCache.put(id, v);
		return v;
	}
}
