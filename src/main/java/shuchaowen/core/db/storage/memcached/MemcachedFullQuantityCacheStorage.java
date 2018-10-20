package shuchaowen.core.db.storage.memcached;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import shuchaowen.core.cache.CAS;
import shuchaowen.core.cache.Memcached;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.db.result.Result;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.db.storage.CommonStorage;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;

/**
 * 
 * @author shuchaowen
 */
public class MemcachedFullQuantityCacheStorage extends CommonStorage {
	private HashSet<String> loadTagMap = new HashSet<String>();
	private static final String SPLIT = "#";
	private final Memcached memcached;

	public MemcachedFullQuantityCacheStorage(AbstractDB db,
			Storage executeStorage, Memcached memcached) {
		super(db, null, executeStorage);
		this.memcached = memcached;
	}

	public void load(Class<?>... tableClass) {
		CountDownLatch countDownLatch = new CountDownLatch(tableClass.length);
		for (Class<?> t : tableClass) {
			Table table = t.getAnnotation(Table.class);
			if (table == null) {
				countDownLatch.countDown();
				continue;
			}
			loadTagMap.add(ClassUtils.getCGLIBRealClassName(t));
			new MemcachedLoadingThread(countDownLatch, this, t).start();
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void loadAll() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		for (Class<?> clz : ClassUtils.getClasses("")) {
			Table table = clz.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			list.add(clz);
		}

		CountDownLatch countDownLatch = new CountDownLatch(list.size());
		for (Class<?> t : list) {
			loadTagMap.add(ClassUtils.getCGLIBRealClassName(t));
			new MemcachedLoadingThread(countDownLatch, this, t).start();
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void casSaveIndex(String key, Object value) {
		boolean b = false;
		while (!b) {
			CAS<LinkedHashMap<Object, Byte>> map = memcached.gets(key);
			if (map == null || map.getValue() == null) {
				LinkedHashMap<Object, Byte> valueMap = new LinkedHashMap<Object, Byte>();
				valueMap.putIfAbsent(value, (byte)0);
				b = memcached.add(key, valueMap);
			} else {
				map.getValue().putIfAbsent(value, (byte)0);
				b = memcached.cas(key, map.getValue(), map.getCas());
			}
		}
	}

	private void casDeleteIndex(String key, Object value) {
		boolean b = false;
		while (!b) {
			CAS<LinkedHashMap<String, Object>> map = memcached.get(key);
			if(map != null && map.getValue() != null){
				map.getValue().remove(value);
			}
			b = memcached.cas(key, map.getValue(), map.getCas());
		}
	}

	public void saveToCache(Object bean) throws IllegalArgumentException,
			IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		sb.append(SPLIT);
		sb.append(tableInfo.getPrimaryKeyColumns()[0].getFieldInfo().forceGet(
				bean));
		for (int i = 1; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			String indexKey = sb.toString();
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo()
					.forceGet(bean);
			sb.append(SPLIT);
			sb.append(v);
			if (i == tableInfo.getPrimaryKeyColumns().length - 1) {
				continue;
			}
			
			casSaveIndex(indexKey, v);
		}
		memcached.add(sb.toString(), CacheUtils.encode(bean));
	}

	public void deleteToCache(Object bean) throws IllegalArgumentException,
			IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		sb.append(SPLIT);
		sb.append(tableInfo.getPrimaryKeyColumns()[0].getFieldInfo().forceGet(
				bean));
		for (int i = 1; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			String indexKey = sb.toString();
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo()
					.forceGet(bean);
			sb.append(SPLIT);
			sb.append(v);
			if (i == tableInfo.getPrimaryKeyColumns().length - 1) {
				continue;
			}

			casDeleteIndex(indexKey, v);
		}
		memcached.delete(sb.toString());
	}

	public void updateToCache(Object bean) throws IllegalArgumentException,
			IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo()
					.forceGet(bean));
		}
		memcached.set(sb.toString(), CacheUtils.encode(bean));
	}

	public void saveOrUpdate(Object bean) throws IllegalArgumentException,
			IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		sb.append(SPLIT);
		sb.append(tableInfo.getPrimaryKeyColumns()[0].getFieldInfo().forceGet(
				bean));
		for (int i = 1; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			String indexKey = sb.toString();
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo()
					.forceGet(bean);
			sb.append(SPLIT);
			sb.append(v);
			if (i == tableInfo.getPrimaryKeyColumns().length - 1) {
				continue;
			}

			casSaveIndex(indexKey, v);
		}
		memcached.set(sb.toString(), CacheUtils.encode(bean));
	}

	@Override
	public void save(Collection<?> beans) {
		super.save(beans);
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			if (loadTagMap.contains(name)) {
				try {
					saveToCache(bean);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void delete(Collection<?> beans) {
		super.delete(beans);
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			if (loadTagMap.contains(name)) {
				try {
					deleteToCache(bean);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void update(Collection<?> beans) {
		super.update(beans);
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			if (loadTagMap.contains(name)) {
				try {
					updateToCache(bean);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void saveOrUpdate(Collection<?> beans) {
		super.saveOrUpdate(beans);
		for (Object bean : beans) {
			String name = ClassUtils.getCGLIBRealClassName(bean.getClass());
			if (loadTagMap.contains(name)) {
				try {
					saveOrUpdate(bean);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public <T> T getById(Class<T> type, Object... params) {
		if (!loadTagMap.contains(type.getName())) {
			return super.getById(type, params);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(type.getName());
		for (Object v : params) {
			sb.append(SPLIT);
			sb.append(v);
		}

		byte[] data = memcached.get(sb.toString());
		if (data == null) {
			return null;
		}

		return CacheUtils.decode(type, data);
	}

	@Override
	public <T> PrimaryKeyValue<T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		if (!loadTagMap.contains(type.getName())) {
			return super.getById(type, primaryKeyParameters);
		}

		Map<String, PrimaryKeyParameter> keyMap = new HashMap<String, PrimaryKeyParameter>();
		for (PrimaryKeyParameter parameter : primaryKeyParameters) {
			StringBuilder sb = new StringBuilder();
			sb.append(type.getName());
			for (Object v : parameter.getParams()) {
				sb.append(SPLIT);
				sb.append(v);
			}
			keyMap.put(sb.toString(), parameter);
		}

		PrimaryKeyValue<T> value = new PrimaryKeyValue<T>();
		Map<String, byte[]> cacheMap = memcached.get(keyMap.keySet());
		if (cacheMap != null) {
			for (Entry<String, byte[]> entry : cacheMap.entrySet()) {
				value.put(keyMap.get(entry.getKey()),
						CacheUtils.decode(type, entry.getValue()));
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		if (!loadTagMap.contains(type.getName())) {
			return super.getByIdList(type, params);
		}

		TableInfo tableInfo = AbstractDB.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length == params.length) {
			T t = getById(type, params);
			return t == null ? null : Arrays.asList(t);
		}

		if (tableInfo.getPrimaryKeyColumns().length == 1) {// 只有一个主键的表不缓存索引
			return super.getByIdList(type, params);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(type.getName());
		for (Object v : params) {
			sb.append(SPLIT);
			sb.append(v);
		}
		
		LinkedHashMap<Object, Byte> map = memcached.get(sb.toString());
		if (map == null || map.isEmpty()) {
			return null;
		}

		String prefix = sb.append(SPLIT).toString();
		List<String> keyList = new ArrayList<String>();
		for(Entry<Object, Byte> entry : map.entrySet()){
			keyList.add(prefix + entry.getKey().toString());
		}
		
		Map<String, byte[]> cacheMap = memcached.get(keyList);
		if (cacheMap == null || cacheMap.isEmpty()) {
			return null;
		}

		List<T> list = new ArrayList<T>();
		for(String k : keyList){
			byte[] data = cacheMap.get(k);
			if (data == null) {
				continue;
			}

			list.add(CacheUtils.decode(type, data));
		}
		return list;
	}
}

class MemcachedLoadingThread extends Thread {
	private final CountDownLatch countDownLatch;
	private final MemcachedFullQuantityCacheStorage fullQuantityCacheStorage;
	private final Class<?> tableClass;

	public MemcachedLoadingThread(CountDownLatch countDownLatch,
			MemcachedFullQuantityCacheStorage fullQuantityCacheStorage,
			Class<?> tableClass) {
		this.countDownLatch = countDownLatch;
		this.fullQuantityCacheStorage = fullQuantityCacheStorage;
		this.tableClass = tableClass;
	}

	@Override
	public void run() {
		try {
			final String name = ClassUtils.getCGLIBRealClassName(tableClass);
			Logger.info("RedisHotSpotCacheStorage", "loading [" + name
					+ "] keys to cache");
			fullQuantityCacheStorage.getDb().iterator(tableClass,
					new ResultIterator() {

						public void next(Result result) {
							Object bean = result.get(tableClass);
							try {
								fullQuantityCacheStorage.saveToCache(bean);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
			Logger.info("RedisHotSpotCacheStorage", "loading [" + name
					+ "] keys to cache success");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			countDownLatch.countDown();
		}
	}
}
