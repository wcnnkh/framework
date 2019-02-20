package scw.sql.orm.plugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import scw.sql.ResultSetMapper;
import scw.sql.Sql;
import scw.sql.SqlOperations;
import scw.sql.SqlUtils;
import scw.sql.orm.result.DefaultResultSet;
import scw.sql.orm.result.ResultSet;

public class SelectCacheManager {
	private final SqlOperations sqlOperations;
	private Map<String, ResultSet> cacheMap;

	public SelectCacheManager(SqlOperations sqlOperations) {
		this.sqlOperations = sqlOperations;
	}

	private ResultSet query(Sql sql) {
		return sqlOperations.query(sql, new ResultSetMapper<ResultSet>() {

			public ResultSet mapper(java.sql.ResultSet resultSet) throws SQLException {
				return new DefaultResultSet(resultSet);
			}
		});
	}

	public ResultSet select(Sql sql) {
		String id = SqlUtils.getSqlId(sql);
		ResultSet resultSet;
		if (cacheMap == null) {
			cacheMap = new HashMap<String, ResultSet>(4, 1);
			resultSet = query(sql);
			cacheMap.put(id, resultSet);
		} else {
			resultSet = cacheMap.get(id);
			if (resultSet == null) {
				resultSet = query(sql);
				cacheMap.put(id, resultSet);
			}
		}
		return resultSet;
	}

	public void clear() {
		cacheMap = null;
	}
}
