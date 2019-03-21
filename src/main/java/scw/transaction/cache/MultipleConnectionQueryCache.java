package scw.transaction.cache;

import java.util.HashMap;
import java.util.Map;

import scw.sql.SqlOperations;
import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;
import scw.transaction.savepoint.Savepoint;

public final class MultipleConnectionQueryCache implements TransactionResource {
	private Map<SqlOperations, QueryCache> queryCacheMap;
	private boolean enable = true;

	public QueryCache getQueryCache(SqlOperations sqlOperations) {
		QueryCache queryCache;
		if (queryCacheMap == null) {
			queryCacheMap = new HashMap<SqlOperations, QueryCache>(4, 1);
			queryCache = new QueryCache(sqlOperations);
			queryCacheMap.put(sqlOperations, queryCache);
		} else {
			queryCache = queryCacheMap.get(sqlOperations);
			if (queryCache == null) {
				queryCache = new QueryCache(sqlOperations);
				queryCacheMap.put(sqlOperations, queryCache);
			}
		}
		return queryCache;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void process() {
	}

	public void rollback() {
	}

	public void end() {
		queryCacheMap = null;
	}

	public Savepoint createSavepoint() throws TransactionException {
		return null;
	}
}
