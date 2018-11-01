package shuchaowen.core.db.storage.cache;

import java.util.Arrays;
import java.util.Collection;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.db.storage.async.AsyncConsumer;
import shuchaowen.core.db.transaction.AbstractTransaction;
import shuchaowen.core.db.transaction.Transaction;

public class CacheAsyncConsumer implements AsyncConsumer {
	private final CacheStorage cacheStorage;

	public CacheAsyncConsumer(CacheStorage cacheStorage) {
		this.cacheStorage = cacheStorage;
	}

	public void consumer(AbstractDB db, Collection<OperationBean> operationBeans) throws Exception {
		TransactionContext.getInstance().begin();
		try {
			db.opToDB(operationBeans);
			for(OperationBean operationBean : operationBeans){
				CacheConfig cacheConfig = cacheStorage.getCacheConfig(operationBean.getBean().getClass());
				switch (cacheConfig.getCacheType()) {
				case keys:
				case lazy:
					boolean exist = dbExist(db, operationBean);
					Transaction transaction = new HostspotDataAsyncRollbackTransaction(exist, cacheConfig.getCacheType() == CacheType.keys, cacheStorage.getCache(), operationBean);
					TransactionContext.getInstance().execute(Arrays.asList(transaction));
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				TransactionContext.getInstance().commit();
			} catch (Throwable e) {
				e.printStackTrace();
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
		cache.hostspotDataAsyncRollback(operationBean, key, exist);
	}
}
