package shuchaowen.core.db.storage.redis;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.result.Result;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.db.storage.CommonStorage;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.XTime;

public class RedisHotSpotCacheStorage extends CommonStorage {
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final int DEFAULT_EXP = (int) ((7 * XTime.ONE_DAY) / 1000);
	private final Map<String, Boolean> loadKeyTagMap = new HashMap<String, Boolean>();
	private static final byte[] NULL_BYTE = new byte[0];

	private final int exp;// 过期时间
	private final Redis redis;

	public RedisHotSpotCacheStorage(AbstractDB db, Redis redis, Storage storage) {
		this(db, DEFAULT_EXP, redis, storage);
	}

	public RedisHotSpotCacheStorage(AbstractDB db, int exp, Redis redis, Storage storage) {
		super(db, null, storage);
		this.exp = exp;
		this.redis = redis;
	}

	public int getExp() {
		return exp;
	}

	public Redis getRedis() {
		return redis;
	}

	/**
	 * 将此表的主键交给缓存来管理
	 * 
	 * @param tableClass
	 */
	public final void loadKeysToCache(Class<?> tableClass) {
		String name = ClassUtils.getCGLIBRealClassName(tableClass);
		if (loadKeyTagMap.containsKey(name)) {
			throw new ShuChaoWenRuntimeException(name + " Already exist");
		}

		synchronized (loadKeyTagMap) {
			if (loadKeyTagMap.containsKey(name)) {
				throw new ShuChaoWenRuntimeException(name + " Already exist");
			}

			loadKeyTagMap.put(name, null);
		}

		// loader
		loadTableKeysToCache(tableClass);
	}

	private String getPrimaryKey(Object bean) {
		try {
			return CacheUtils.getObjectPrimaryKey(bean);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	protected void loadTableKeysToCache(final Class<?> tableClass) {
		final String name = ClassUtils.getCGLIBRealClassName(tableClass);
		Logger.info("loading [" + name + "] keys to cache");
		getDb().iterator(tableClass, new ResultIterator() {

			public void next(Result result) {
				Object bean = result.get(tableClass);
				String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
				redis.hsetnx(name.getBytes(DEFAULT_CHARSET), getPrimaryKey(bean).getBytes(DEFAULT_CHARSET),
						CacheUtils.encode(bean));
			}
		});
	}

	public <T> T getByIdFromCache(Class<T> type, Object... params) {
		byte[] key = (CacheUtils.getObjectKey(type, params)).getBytes(DEFAULT_CHARSET);
		byte[] data = redis.get(key);
		if (data == null) {
			return null;
		}

		redis.expire(key, exp);
		return CacheUtils.decode(type, data);
	}

	public <T> Map<PrimaryKeyParameter, T> getByIdFromCache(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		Map<byte[], PrimaryKeyParameter> keyMap = new HashMap<byte[], PrimaryKeyParameter>();
		for (PrimaryKeyParameter parameter : primaryKeyParameters) {
			keyMap.put(CacheUtils.getObjectKey(type, parameter).getBytes(DEFAULT_CHARSET), parameter);
		}

		Map<byte[], byte[]> map = redis.get(keyMap.keySet().toArray(new byte[keyMap.size()][]));
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<PrimaryKeyParameter, T> dataMap = new HashMap<PrimaryKeyParameter, T>();
		for (Entry<byte[], byte[]> entry : map.entrySet()) {
			dataMap.put(keyMap.get(entry.getKey()), CacheUtils.decode(type, entry.getValue()));
		}
		return dataMap;
	}

	public void saveToCache(Collection<?> beans) {
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			byte[] key = getObjectKey(bean).getBytes(DEFAULT_CHARSET);
			redis.setex(key, exp, CacheUtils.encode(bean));
			if (loadKeyTagMap.containsKey(name)) {
				redis.hsetnx(name.getBytes(DEFAULT_CHARSET), getPrimaryKey(bean).getBytes(DEFAULT_CHARSET), NULL_BYTE);
			}
		}
	}

	public void updateToCache(Collection<?> beans) {
		for (Object bean : beans) {
			redis.setex(getObjectKey(bean).getBytes(DEFAULT_CHARSET), exp, CacheUtils.encode(bean));
		}
	}

	public void saveOrUpdateToCache(Collection<?> beans) {
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			byte[] key = getObjectKey(bean).getBytes(DEFAULT_CHARSET);
			redis.setex(key, exp, CacheUtils.encode(bean));
			if (loadKeyTagMap.containsKey(name)) {
				redis.hsetnx(name.getBytes(DEFAULT_CHARSET), getPrimaryKey(bean).getBytes(DEFAULT_CHARSET), NULL_BYTE);
			}
		}
	}

	public void deleteToCache(Collection<?> beans) {
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			byte[] key = getObjectKey(bean).getBytes(DEFAULT_CHARSET);
			redis.delete(key);
			if (loadKeyTagMap.containsKey(name)) {
				redis.hdel(name.getBytes(DEFAULT_CHARSET), key);
			}
		}
	}

	protected String getObjectKey(Object bean) {
		try {
			return CacheUtils.getObjectKey(bean);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getById(Class<T> type, Object... params) {
		Object t = getByIdFromCache(type, params);
		if (t != null) {
			return (T) t;
		}

		// 用于防止大量无效的请求而导致的数据库压力过大，要处理因缓存穿透导致的问题应该使用
		String name = ClassUtils.getCGLIBRealClassName(type);
		if (loadKeyTagMap.containsKey(name)) {
			if (redis.hexists(name.getBytes(DEFAULT_CHARSET),
					CacheUtils.getObjectPrimaryKey(type, params).getBytes(DEFAULT_CHARSET))) {
				return null;
			}
		}

		t = super.getById(type, params);
		if (t != null) {
			saveToCache(Arrays.asList(t));
		}
		return (T) t;
	}

	@Override
	public <T> Map<PrimaryKeyParameter, T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		Map<byte[], PrimaryKeyParameter> keyMap = new HashMap<byte[], PrimaryKeyParameter>();
		for (PrimaryKeyParameter parameter : primaryKeyParameters) {
			keyMap.put(CacheUtils.getObjectKey(type, parameter).getBytes(DEFAULT_CHARSET), parameter);
		}

		Map<byte[], byte[]> cacheMap = redis.get(keyMap.keySet().toArray(new byte[keyMap.size()][]));
		Map<PrimaryKeyParameter, T> map = new HashMap<PrimaryKeyParameter, T>();
		if (cacheMap != null && !cacheMap.isEmpty()) {
			for (Entry<byte[], byte[]> entry : cacheMap.entrySet()) {
				map.put(keyMap.get(entry.getKey()), CacheUtils.decode(type, entry.getValue()));
			}
		}

		if (cacheMap == null || cacheMap.size() != primaryKeyParameters.size()) {
			List<byte[]> notFindList = new ArrayList<byte[]>();
			for (Entry<byte[], PrimaryKeyParameter> entry : keyMap.entrySet()) {
				if (cacheMap == null || cacheMap.containsKey(entry.getKey())) {
					continue;
				}

				notFindList.add(entry.getKey());
			}

			String name = ClassUtils.getCGLIBRealClassName(type);
			if (loadKeyTagMap.containsKey(name)) {
				byte[] k = name.getBytes(DEFAULT_CHARSET);
				Iterator<byte[]> iterator = notFindList.iterator();
				while (iterator.hasNext()) {
					byte[] key = iterator.next();
					if (redis.hexists(k,
							CacheUtils.getObjectPrimaryKey(type, keyMap.get(key)).getBytes(DEFAULT_CHARSET))) {
						iterator.remove();
					}
				}
			}

			List<PrimaryKeyParameter> list = new ArrayList<PrimaryKeyParameter>();
			for (byte[] key : notFindList) {
				list.add(keyMap.get(key));
			}

			Map<PrimaryKeyParameter, T> dbMap = super.getById(type, list);
			if (dbMap != null && !dbMap.isEmpty()) {
				map.putAll(dbMap);
				saveToCache(dbMap.values());
			}
		}
		return map;
	}

	@Override
	public void save(Collection<?> beans) {
		super.save(beans);
		saveToCache(beans);
	}

	@Override
	public void delete(Collection<?> beans) {
		super.delete(beans);
		deleteToCache(beans);
	}

	@Override
	public void update(Collection<?> beans) {
		super.update(beans);
		updateToCache(beans);
	}

	@Override
	public void saveOrUpdate(Collection<?> beans) {
		super.saveOrUpdate(beans);
		saveOrUpdateToCache(beans);
	}
}
