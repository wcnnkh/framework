package shuchaowen.core.db.storage.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.db.result.Result;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.db.storage.async.AbstractAsyncStorage;
import shuchaowen.core.db.storage.async.AsyncConsumer;
import shuchaowen.core.db.storage.async.MemcachedAsyncStorage;
import shuchaowen.core.db.storage.async.RedisAsyncStorage;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.XTime;
import shuchaowen.memcached.Memcached;
import shuchaowen.redis.Redis;

public class CacheStorage implements Storage {
	private static final int DATA_DEFAULT_EXP_TIME = 7 * ((int) XTime.ONE_DAY / 1000);
	private static final CacheConfig DEFAULT_CACHE_CONFIG = new CacheConfig(CacheType.lazy, DATA_DEFAULT_EXP_TIME,
			false);
	private final Map<String, CacheConfig> cacheConfigMap = new HashMap<String, CacheConfig>();
	private final AbstractAsyncStorage asyncStorage;
	private final Cache cache;
	private final CacheHelper cacheHelper;
	private final AbstractDB db;

	public CacheStorage(Cache cache, AbstractAsyncStorage asyncStorage) {
		this.db = asyncStorage.getDb();
		this.cache = cache;
		this.asyncStorage = asyncStorage;
		this.cacheHelper = new CacheHelper(cache);
	}
	
	public CacheStorage(AbstractDB db, Memcached memcached, String queueKey, AsyncConsumer asyncConsumer){
		this.db = db;
		this.cache = new MemcachedCache(memcached);
		this.asyncStorage = new MemcachedAsyncStorage(db, memcached, queueKey, asyncConsumer);
		this.cacheHelper = new CacheHelper(cache);
	}
	
	public CacheStorage(AbstractDB db, Redis redis, String queueKey, AsyncConsumer asyncConsumer){
		this.db = db;
		this.cache = new RedisCache(redis);
		this.asyncStorage = new RedisAsyncStorage(db, redis, queueKey, asyncConsumer);
		this.cacheHelper = new CacheHelper(cache);
	}
	
	public CacheStorage(AbstractDB db, Memcached memcached, String queueKey){
		this.db = db;
		this.cache = new MemcachedCache(memcached);
		this.asyncStorage = new MemcachedAsyncStorage(db, memcached, queueKey, new CacheAsyncConsumer(this));
		this.cacheHelper = new CacheHelper(cache);
	}
	
	public CacheStorage(AbstractDB db, Redis redis, String queueKey){
		this.db = db;
		this.cache = new RedisCache(redis);
		this.asyncStorage = new RedisAsyncStorage(db, redis, queueKey, new CacheAsyncConsumer(this));
		this.cacheHelper = new CacheHelper(cache);
	}

	public AbstractAsyncStorage getAsyncStroage() {
		return asyncStorage;
	}

	public AbstractDB getDB() {
		return db;
	}

	public Cache getCache() {
		return cache;
	}

	public void config(CacheType cacheType, int exp, boolean isAsync, Class<?>... tableClass) {
		config(new CacheConfig(cacheType, exp, isAsync), tableClass);
	}

	public void config(CacheType cacheType, boolean isAsync, Class<?>... tableClass) {
		config(new CacheConfig(cacheType, DATA_DEFAULT_EXP_TIME, isAsync), tableClass);
	}

	public void config(CacheConfig config, Class<?>... tableClass) {
		for (Class<?> t : tableClass) {
			Table table = t.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			String name = ClassUtils.getCGLIBRealClassName(t);
			cacheConfigMap.put(name, config);
		}
	}

	public void init() {
		CountDownLatch countDownLatch = new CountDownLatch(cacheConfigMap.size());
		for (Entry<String, CacheConfig> entry : cacheConfigMap.entrySet()) {
			try {
				new LoadingThread(countDownLatch, this, ClassUtils.forName(entry.getKey())).start();
				;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected CacheConfig getCacheConfig(Class<?> tableClass) {
		CacheConfig cacheInfo = cacheConfigMap.get(ClassUtils.getCGLIBRealClassName(tableClass));
		return cacheInfo == null ? DEFAULT_CACHE_CONFIG : cacheInfo;
	}

	public <T> T getById(Class<T> type, Object... params) {
		T t = null;
		CacheConfig cacheInfo = getCacheConfig(type);
		try {
			switch (cacheInfo.getCacheType()) {
			case lazy:
				t = cache.getById(getDB(), false, cacheInfo.getExp(), type, params);
				break;
			case keys:
				t = cache.getById(getDB(), true, cacheInfo.getExp(), type, params);
			case full:
				t = cache.getById(type, params);
			default:
				t = getDB().getByIdFromDB(type, null, params);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public <T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters) {
		CacheConfig cacheInfo = getCacheConfig(type);
		try {
			switch (cacheInfo.getCacheType()) {
			case lazy:
				return cache.getById(getDB(), false, type, primaryKeyParameters);
			case keys:
				return cache.getById(getDB(), true, type, primaryKeyParameters);
			case full:
				return cache.getById(type, primaryKeyParameters);
			default:
				return getDB().getByIdFromDB(type, null, primaryKeyParameters);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		CacheConfig cacheInfo = getCacheConfig(type);
		switch (cacheInfo.getCacheType()) {
		case full:
			try {
				return cache.getByIdList(getDB(), type, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		default:
			return getDB().getByIdListFromDB(type, null, params);
		}
	}
	
	public void save(Collection<?> beans) {
		for (Object bean : beans) {
			CacheConfig cacheInfo = getCacheConfig(bean.getClass());
			cacheHelper.saveToCache(bean, cacheInfo);

			if (cacheInfo.isAsync()) {
				asyncStorage.save(Arrays.asList(bean));
			} else {
				getDB().saveToDB(Arrays.asList(bean));
			}
		}
	}

	public void update(Collection<?> beans) {
		for (Object bean : beans) {
			CacheConfig cacheInfo = getCacheConfig(bean.getClass());
			cacheHelper.updateToCache(bean, cacheInfo);

			if (cacheInfo.isAsync()) {
				asyncStorage.update(Arrays.asList(bean));
			} else {
				getDB().updateToDB(Arrays.asList(bean));
			}
		}
	}

	public void delete(Collection<?> beans) {
		for (Object bean : beans) {
			CacheConfig cacheInfo = getCacheConfig(bean.getClass());
			cacheHelper.deleteToCache(bean, cacheInfo);

			if (cacheInfo.isAsync()) {
				asyncStorage.delete(Arrays.asList(bean));
			} else {
				getDB().deleteToDB(Arrays.asList(bean));
			}
		}
	}

	public void saveOrUpdate(Collection<?> beans) {
		for (Object bean : beans) {
			CacheConfig cacheInfo = getCacheConfig(bean.getClass());
			cacheHelper.saveOrUpdateToCache(bean, cacheInfo);

			if (cacheInfo.isAsync()) {
				asyncStorage.saveOrUpdate(Arrays.asList(bean));
			} else {
				getDB().saveOrUpdateToDB(Arrays.asList(bean));
			}
		}
	}

	protected void loadCache(final Class<?> tableClass) {
		final CacheConfig cacheInfo = getCacheConfig(tableClass);
		getDB().iterator(tableClass, new ResultIterator() {

			public void next(Result result) {
				Object bean = result.get(tableClass);
				try {
					switch (cacheInfo.getCacheType()) {
					case full:
						cache.loadBeanAndIndex(bean);
						break;
					case keys:
						cache.loadBeanAndKey(bean);
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

class LoadingThread extends Thread {
	private final CountDownLatch countDownLatch;
	private final CacheStorage cacheStorage;
	private final Class<?> tableClass;

	public LoadingThread(CountDownLatch countDownLatch, CacheStorage cacheStorage, Class<?> tableClass) {
		this.countDownLatch = countDownLatch;
		this.tableClass = tableClass;
		this.cacheStorage = cacheStorage;
	}

	@Override
	public void run() {
		try {
			final String name = ClassUtils.getCGLIBRealClassName(tableClass);
			Logger.info("RedisHotSpotCacheStorage", "loading [" + name + "] keys to cache");
			cacheStorage.loadCache(tableClass);
			Logger.info("RedisHotSpotCacheStorage", "loading [" + name + "] keys to cache success");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			countDownLatch.countDown();
		}
	}
}
