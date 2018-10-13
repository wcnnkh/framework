package shuchaowen.core.db.storage.memcached;

import shuchaowen.core.cache.Memcached;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.AbstractCacheStorage;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.db.storage.cache.MemcachedBinaryCache;
import shuchaowen.core.db.storage.cache.MemcachedCache;
import shuchaowen.core.util.XTime;

public class MemcachedHotSpotCacheStorage extends AbstractCacheStorage{
	private static final int DEFAULT_EXP = (int) ((7 * XTime.ONE_DAY) / 1000);
	
	public MemcachedHotSpotCacheStorage(AbstractDB db, Memcached memcached, Storage storage){
		super(db, new MemcachedBinaryCache(memcached), "", DEFAULT_EXP, storage);
	}
	
	public MemcachedHotSpotCacheStorage(AbstractDB db, String prefix, int exp, Memcached memcached, Storage storage){
		super(db, new MemcachedBinaryCache(memcached), prefix, exp, storage);
	}
	
	public MemcachedHotSpotCacheStorage(AbstractDB db, String prefix, int exp, MemcachedCache memcachedCache, Storage storage){
		super(db, memcachedCache, prefix, exp, storage);
	}
}
