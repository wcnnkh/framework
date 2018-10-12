package shuchaowen.core.db.storage.memcached;

import shuchaowen.core.cache.Memcached;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.AbstractExecuteStorage;
import shuchaowen.core.db.storage.AbstractHotSpotDataCacheStorage;
import shuchaowen.core.db.storage.async.MemoryAsyncExecuteStorage;
import shuchaowen.core.util.XTime;

public class MemcachedHotSpotDataCacheStorage extends AbstractHotSpotDataCacheStorage{
	private static final int DEFAULT_EXP = (int) ((7 * XTime.ONE_DAY) / 1000);
	private Memcached memcached;
	
	/**
	 * 此方案只能用于单服务器架构，集群架构请不要使用此构造方法
	 * 异步存储方案使用的是MemoryAsyncExecuteStorage
	 * @param db
	 * @param memcached
	 */
	public MemcachedHotSpotDataCacheStorage(AbstractDB db, Memcached memcached){
		this(new MemoryAsyncExecuteStorage(db), memcached);
	}
	
	public MemcachedHotSpotDataCacheStorage(AbstractExecuteStorage abstractExecuteStorage, Memcached memcached){
		this(abstractExecuteStorage, "", DEFAULT_EXP, memcached);
	}
	
	public MemcachedHotSpotDataCacheStorage(AbstractExecuteStorage abstractExecuteStorage, String prefix, int exp, Memcached memcached){
		super(abstractExecuteStorage, prefix, exp);
		this.memcached = memcached;
	}

	@Override
	public <T> T getAndTouch(Class<T> type, String key, int exp) throws Exception{
		T t = memcached.get(key);;
		if (t == null) {
			return t;
		}

		if (exp > 0) {
			memcached.set(key, exp, t);
		}
		return t;
	}

	@Override
	public void set(String key, int exp, Object data) throws Exception{
		memcached.add(key, exp, data);
	}

	@Override
	public boolean add(String key, int exp, Object data) throws Exception{
		return memcached.set(key, exp, data);
	}

	@Override
	public boolean delete(String key) throws Exception{
		return memcached.delete(key);
	}
}
