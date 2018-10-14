package shuchaowen.core.db.storage.redis;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.storage.AbstractCacheStorage;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.util.XTime;

public class RedisHotSpotCacheStorage extends AbstractCacheStorage {
	private static final int DEFAULT_EXP = (int) ((7 * XTime.ONE_DAY) / 1000);
	private final String prefix;
	private final int exp;// 过期时间
	private final Redis redis;

	public RedisHotSpotCacheStorage(AbstractDB db, Redis redis, Storage execute) {
		this(db, "", DEFAULT_EXP, redis, execute);
	}

	public RedisHotSpotCacheStorage(AbstractDB db, String prefix, int exp,
			Redis redis, Storage execute) {
		super(db, execute);
		this.prefix = prefix;
		this.exp = exp;
		this.redis = redis;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getExp() {
		return exp;
	}

	public Redis getRedis() {
		return redis;
	}

	@Override
	public void deleteToCache(Collection<?> beans) {
		byte[][] keys = new byte[beans.size()][];
		int i = 0;
		try {
			for (Object bean : beans) {
				keys[i] = (prefix + CacheUtils.getObjectKey(bean))
						.getBytes("UTF-8");

			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		redis.delete(keys);
	}

	@Override
	public <T> T getByIdFromCache(Class<T> type, Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<PrimaryKeyParameter, T> getByIdFromCache(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveToCache(Collection<?> beans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateToCache(Collection<?> beans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveOrUpdateToCache(Collection<?> beans) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getByIdExist(Class<?> type, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}
}
