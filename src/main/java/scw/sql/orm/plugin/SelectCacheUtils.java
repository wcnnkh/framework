package scw.sql.orm.plugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.sql.ResultSetMapper;
import scw.sql.Sql;
import scw.sql.SqlOperations;
import scw.sql.orm.result.DefaultResultSet;
import scw.sql.orm.result.ResultSet;

public abstract class SelectCacheUtils {
	private static final ThreadLocal<Map<SqlOperations, SelectCacheManager>> LOCAL = new ThreadLocal<Map<SqlOperations, SelectCacheManager>>();
	private static final ThreadLocal<LinkedList<Boolean>> TAG_LOCAL = new ThreadLocal<LinkedList<Boolean>>();

	public static boolean isEnable() {
		LinkedList<Boolean> list = TAG_LOCAL.get();
		if (list == null) {
			return false;
		}

		Boolean b = list.getLast();
		return b == null ? false : b;
	}

	protected static void begin(boolean enable) {
		LinkedList<Boolean> list = TAG_LOCAL.get();
		if (list == null) {
			list = new LinkedList<Boolean>();
			TAG_LOCAL.set(list);
		}

		list.add(enable);
	}

	protected static void end() {
		LinkedList<Boolean> list = TAG_LOCAL.get();
		if (list != null) {
			if (list.size() == 1) {
				TAG_LOCAL.remove();
				LOCAL.remove();
			}
		}
	}

	public static void setEnable(boolean enable) {
		LinkedList<Boolean> list = TAG_LOCAL.get();
		if (list == null || list.isEmpty()) {
			return;
		}

		list.set(list.size() - 1, enable);
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
}
