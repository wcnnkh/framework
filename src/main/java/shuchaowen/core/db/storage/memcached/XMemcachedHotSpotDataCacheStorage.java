package shuchaowen.core.db.storage.memcached;

import net.rubyeye.xmemcached.MemcachedClient;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.storage.AbstractExecuteStorage;
import shuchaowen.core.db.storage.AbstractHotSpotDataCacheStorage;
import shuchaowen.core.util.XTime;

public class XMemcachedHotSpotDataCacheStorage extends AbstractHotSpotDataCacheStorage{
	private static final int DEFAULT_EXP = (int) ((7 * XTime.ONE_DAY) / 1000);
	private final MemcachedClient memcachedClient;
	

	public XMemcachedHotSpotDataCacheStorage(AbstractDB db, SQLFormat sqlFormat, MemcachedClient memcachedClient) {
		this(db, sqlFormat, "", DEFAULT_EXP, memcachedClient);
	}
	
	public XMemcachedHotSpotDataCacheStorage(AbstractDB db, SQLFormat sqlFormat, String prefix, int exp, MemcachedClient memcachedClient){
		super(db, sqlFormat, prefix, exp);
		this.memcachedClient = memcachedClient;
	}
	
	public XMemcachedHotSpotDataCacheStorage(AbstractExecuteStorage abstractExecuteStorage, MemcachedClient memcachedClient){
		this(abstractExecuteStorage, "", (int) ((7 * XTime.ONE_DAY) / 1000), memcachedClient);
	}
	
	public XMemcachedHotSpotDataCacheStorage(AbstractExecuteStorage abstractExecuteStorage, String prefix, int exp, MemcachedClient memcachedClient){
		super(abstractExecuteStorage, prefix, exp);
		this.memcachedClient = memcachedClient;
	}

	@Override
	public <T> T getAndTouch(Class<T> type, String key, int exp) throws Exception{
		T t = memcachedClient.get(key);;
		if (t == null) {
			return t;
		}

		if (exp > 0) {
			memcachedClient.set(key, exp, t);
		}
		return t;
	}

	@Override
	public void set(String key, int exp, Object data) throws Exception{
		memcachedClient.add(key, exp, data);
	}

	@Override
	public boolean add(String key, int exp, Object data) throws Exception{
		return memcachedClient.set(key, exp, data);
	}

	@Override
	public boolean delete(String key) throws Exception{
		return memcachedClient.delete(key);
	}
}
