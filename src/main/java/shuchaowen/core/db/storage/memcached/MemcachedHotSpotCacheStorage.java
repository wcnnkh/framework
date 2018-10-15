package shuchaowen.core.db.storage.memcached;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.cache.Memcached;
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

public class MemcachedHotSpotCacheStorage extends CommonStorage {
	private static final int DEFAULT_EXP = (int) ((7 * XTime.ONE_DAY) / 1000);
	private static final String INDEX_PREFIX = "index#";
	private final Map<String, Boolean> loadKeyTagMap = new HashMap<String, Boolean>();
	private static final byte[] NULL_BYTE = new byte[0];

	private final int exp;// 过期时间
	private final Memcached memcached;

	public MemcachedHotSpotCacheStorage(AbstractDB db, Memcached memcached, Storage storage) {
		this(db, DEFAULT_EXP, memcached, storage);
	}

	public MemcachedHotSpotCacheStorage(AbstractDB db, int exp, Memcached memcached, Storage storage) {
		super(db, null, storage);
		this.exp = exp;
		this.memcached = memcached;
	}

	public int getExp() {
		return exp;
	}

	public Memcached getMemcached() {
		return memcached;
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

	protected void loadTableKeysToCache(final Class<?> tableClass) {
		final String name = ClassUtils.getCGLIBRealClassName(tableClass);
		Logger.info("loading [" + name + "] keys to cache");
		getDb().iterator(tableClass, new ResultIterator() {

			public void next(Result result) {
				Object bean = result.get(tableClass);
				memcached.set(INDEX_PREFIX + getObjectKey(bean), NULL_BYTE);
			}
		});
	}

	public <T> T getByIdFromCache(Class<T> type, Object... params) {
		byte[] data = memcached.getAndTocuh(CacheUtils.getObjectKey(type, params), exp);
		if(data == null){
			return null;
		}
		
		return CacheUtils.decode(type, data);
	}

	public void saveToCache(Collection<?> beans) {
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			String key = getObjectKey(bean);
			memcached.add(key, exp, CacheUtils.encode(bean));
			if (loadKeyTagMap.containsKey(name)) {
				memcached.add(INDEX_PREFIX + key, NULL_BYTE);
			}
		}
	}

	public void updateToCache(Collection<?> beans){
		for (Object bean : beans) {
			memcached.add(getObjectKey(bean), exp, CacheUtils.encode(bean));
		}
	}

	public void saveOrUpdateToCache(Collection<?> beans){
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			String key = getObjectKey(bean);
			memcached.set(key, exp, CacheUtils.encode(bean));
			if (loadKeyTagMap.containsKey(name)) {
				memcached.add(INDEX_PREFIX + key, NULL_BYTE);
			}
		}
	}

	public void deleteToCache(Collection<?> beans) {
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			String key = getObjectKey(bean);
			memcached.delete(key);
			if (loadKeyTagMap.containsKey(name)) {
				memcached.delete(INDEX_PREFIX + key);
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
		if(loadKeyTagMap.containsKey(name)){
			if(memcached.get(INDEX_PREFIX + CacheUtils.getObjectKey(type, params)) != null){
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
		Map<String, PrimaryKeyParameter> keyMap = new HashMap<String, PrimaryKeyParameter>();
		for(PrimaryKeyParameter parameter : primaryKeyParameters){
			keyMap.put(CacheUtils.getObjectKey(type, parameter), parameter);
		}
		
		Map<PrimaryKeyParameter, T> map = new HashMap<PrimaryKeyParameter, T>();
		Map<String, byte[]> cacheMap = memcached.get(keyMap.keySet());
		if(cacheMap != null && !cacheMap.isEmpty()){
			for(Entry<String, byte[]> entry : cacheMap.entrySet()){
				map.put(keyMap.get(entry.getKey()), CacheUtils.decode(type, entry.getValue()));
			}
		}
		
		if(cacheMap == null || cacheMap.size() != primaryKeyParameters.size()){
			List<String> notFindList = new ArrayList<String>();
			for(Entry<String, PrimaryKeyParameter> entry : keyMap.entrySet()){
				if(cacheMap == null || cacheMap.containsKey(entry.getKey())){
					continue;
				}
				
				notFindList.add(entry.getKey());
			}
			
			String name = ClassUtils.getCGLIBRealClassName(type);
			if(loadKeyTagMap.containsKey(name)){
				//key是有缓存的
				Map<String, byte[]> existMap = memcached.get(notFindList);
				if(existMap != null){
					Iterator<String> iterator = notFindList.iterator();
					while(iterator.hasNext()){
						String key = iterator.next();
						if(existMap.containsKey(key)){
							iterator.remove();
						}
					}
				}
			}
			
			List<PrimaryKeyParameter> list = new ArrayList<PrimaryKeyParameter>();
			for(String key : notFindList){
				list.add(keyMap.get(key));
			}
			
			Map<PrimaryKeyParameter, T> dbMap = super.getById(type, list);
			if(dbMap != null && !dbMap.isEmpty())
			{	
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
