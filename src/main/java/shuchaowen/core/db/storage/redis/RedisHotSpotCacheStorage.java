package shuchaowen.core.db.storage.redis;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.AbstractCacheStorage;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.db.storage.cache.RedisCache;
import shuchaowen.core.util.XTime;

public class RedisHotSpotCacheStorage extends AbstractCacheStorage{
	private static final int DEFAULT_EXP = (int) ((7 * XTime.ONE_DAY) / 1000);
	
	public RedisHotSpotCacheStorage(AbstractDB db, Redis redis, Storage execute){
		super(db, new RedisCache(redis), "", DEFAULT_EXP, execute);
	}
	
	public RedisHotSpotCacheStorage(AbstractDB db, String prefix, int exp, Redis redis, Storage execute){
		super(db, new RedisCache(redis), prefix, exp, execute);
	}
}
