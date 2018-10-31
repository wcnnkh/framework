package shuchaowen.core.db.storage.cache;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.db.storage.async.AsyncConsumer;

public class CacheAsyncConsumer implements AsyncConsumer{
	private final CacheStorage cacheStorage;
	
	public CacheAsyncConsumer(CacheStorage cacheStorage){
		this.cacheStorage = cacheStorage;
	}
	
	public void consumer(AbstractDB db, ExecuteInfo executeInfo) {
		TransactionContext.getInstance().begin();
		try {
			//TODO
			TransactionContext.getInstance().execute(db, executeInfo.getSqlList(db));
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				TransactionContext.getInstance().commit();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
