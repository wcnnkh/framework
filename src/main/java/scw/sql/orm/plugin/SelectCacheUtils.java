package scw.sql.orm.plugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import scw.sql.ResultSetMapper;
import scw.sql.Sql;
import scw.sql.SqlOperations;
import scw.sql.orm.result.DefaultResultSet;
import scw.sql.orm.result.ResultSet;

public abstract class SelectCacheUtils {
	private static final ThreadLocal<Map<SqlOperations, SelectCacheManager>> LOCAL = new ThreadLocal<Map<SqlOperations, SelectCacheManager>>();
	private static final ThreadLocal<Boolean> TAG_LOCAL = new ThreadLocal<Boolean>();

	public static boolean isEnable() {
		Boolean b = TAG_LOCAL.get();
		return b == null ? false : b;
	}

	public static void setEnable(boolean enable) {
		TAG_LOCAL.set(enable);
	}

	public static ResultSet select(SqlOperations sqlOperations, Sql sql) {
		if (isEnable()) {
			Map<SqlOperations, SelectCacheManager> map = LOCAL.get();
			SelectCacheManager manager;
			if (map == null) {
				map = new HashMap<SqlOperations, SelectCacheManager>(4, 1);
				manager = new SelectCacheManager(sqlOperations);
				map.put(sqlOperations, manager);
				LOCAL.set(map);
			} else {
				manager = map.get(sqlOperations);
				if (manager == null) {
					manager = new SelectCacheManager(sqlOperations);
					map.put(sqlOperations, manager);
				}
			}
			return manager.select(sql);
		} else {
			return sqlOperations.query(sql, new ResultSetMapper<ResultSet>() {

				public ResultSet mapper(java.sql.ResultSet resultSet) throws SQLException {
					return new DefaultResultSet(resultSet);
				}
			});
		}
	}

	public static void clear() {
		LOCAL.remove();

	}
}
