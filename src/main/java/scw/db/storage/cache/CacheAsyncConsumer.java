package scw.db.storage.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.Logger;
import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.Transaction;
import scw.common.transaction.TransactionCollection;
import scw.database.DataBaseUtils;
import scw.database.SQL;
import scw.database.TableInfo;
import scw.database.TransactionContext;
import scw.db.AbstractDB;
import scw.db.DBUtils;
import scw.db.OperationBean;
import scw.db.storage.CacheUtils;

public class CacheAsyncConsumer {
	private final CacheStorage cacheStorage;

	public CacheAsyncConsumer(CacheStorage cacheStorage) {
		this.cacheStorage = cacheStorage;
	}

	private boolean dbExist(AbstractDB abstractDB, OperationBean operationBean)
			throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(operationBean.getBean().getClass());
		Object[] params = new Object[tableInfo.getPrimaryKeyColumns().length];
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			params[i] = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(operationBean.getBean());
		}

		return abstractDB.getById(tableInfo.getClassInfo().getClz(), params) != null;
	}

	public void handler(Collection<OperationBean> message) throws Exception {
		TransactionContext.getInstance().begin();
		try {
			Collection<SQL> sqls = DBUtils.getSqlList(cacheStorage.getDB().getSqlFormat(), message);
			if (sqls == null || sqls.isEmpty()) {
				return;
			}

			TransactionCollection collection = new TransactionCollection();
			for (OperationBean operationBean : message) {
				CacheConfig cacheConfig = cacheStorage.getCacheConfig(operationBean.getBean().getClass());
				switch (cacheConfig.getCacheType()) {
				case keys:
				case lazy:
					boolean exist = dbExist(cacheStorage.getDB(), operationBean);
					Transaction transaction = new HostspotDataAsyncRollbackTransaction(exist,
							cacheConfig.getCacheType() == CacheType.keys, cacheStorage.getCache(), operationBean);
					collection.add(transaction);
					break;
				default:
					break;
				}
			}

			TransactionContext.getInstance().execute(cacheStorage.getDB(), sqls, collection);
		} catch (Exception e) {
			throw e;
		} finally {
			TransactionContext.getInstance().end();
		}
	}
}

class HostspotDataAsyncRollbackTransaction extends AbstractTransaction {
	private final Cache cache;
	private final OperationBean operationBean;
	private final boolean key;
	private final boolean exist;

	public HostspotDataAsyncRollbackTransaction(boolean exist, boolean key, Cache cache, OperationBean operationBean) {
		this.cache = cache;
		this.operationBean = operationBean;
		this.exist = exist;
		this.key = key;
	}

	public void begin() throws Exception {
	}

	public void process() throws Exception {
	}

	public void end() throws Exception {
	}

	public void rollback() throws Exception {
		Map<String, Object> map = CacheUtils.getObjectProperties(operationBean.getBean());
		StringBuilder sb = new StringBuilder();
		sb.append(operationBean.getBean().getClass().getName());
		sb.append("{");
		if (map != null) {
			Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());

				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append("}");
		}
		Logger.debug("CacheAsyncConsumer-rollback-" + operationBean.getOperationType().name(), sb.toString());
		cache.hostspotDataAsyncRollback(operationBean, key, exist);
	}
}
