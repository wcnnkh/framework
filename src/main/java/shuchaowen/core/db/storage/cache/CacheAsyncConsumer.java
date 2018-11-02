package shuchaowen.core.db.storage.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DBUtils;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.db.storage.async.AsyncConsumer;
import shuchaowen.core.db.transaction.AbstractTransaction;
import shuchaowen.core.db.transaction.Transaction;
import shuchaowen.core.db.transaction.TransactionCollection;
import shuchaowen.core.util.Logger;

public class CacheAsyncConsumer implements AsyncConsumer {
	private final CacheStorage cacheStorage;

	public CacheAsyncConsumer(CacheStorage cacheStorage) {
		this.cacheStorage = cacheStorage;
	}

	public void consumer(AbstractDB db, Collection<OperationBean> operationBeans) throws Exception {
		TransactionContext.getInstance().begin();
		try {
			Collection<SQL> sqls = DBUtils.getSqlList(db.getSqlFormat(), operationBeans);
			if (sqls == null || sqls.isEmpty()) {
				return;
			}

			TransactionCollection collection = new TransactionCollection();
			for (OperationBean operationBean : operationBeans) {
				CacheConfig cacheConfig = cacheStorage.getCacheConfig(operationBean.getBean().getClass());
				switch (cacheConfig.getCacheType()) {
				case keys:
				case lazy:
					boolean exist = dbExist(db, operationBean);
					Transaction transaction = new HostspotDataAsyncRollbackTransaction(exist,
							cacheConfig.getCacheType() == CacheType.keys, cacheStorage.getCache(), operationBean);
					collection.add(transaction);
					break;
				default:
					break;
				}
			}

			TransactionContext.getInstance().execute(db, sqls, collection);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				TransactionContext.getInstance().commit();
			} catch (Throwable e) {
				throw new Exception(e);
			}
		}
	}

	private boolean dbExist(AbstractDB abstractDB, OperationBean operationBean)
			throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(operationBean.getBean().getClass());
		Object[] params = new Object[tableInfo.getPrimaryKeyColumns().length];
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			params[i] = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(operationBean.getBean());
		}

		return abstractDB.getByIdFromDB(tableInfo.getClassInfo().getClz(), null, params) != null;
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
