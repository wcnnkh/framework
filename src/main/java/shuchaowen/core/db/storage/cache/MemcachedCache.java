package shuchaowen.core.db.storage.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.OperationType;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.db.transaction.Transaction;
import shuchaowen.memcached.Memcached;

public class MemcachedCache implements Cache {
	private final Memcached memcached;

	public MemcachedCache(Memcached memcached) {
		this.memcached = memcached;
	}

	public Memcached getMemcached() {
		return memcached;
	}

	public void loadFull(Object bean) throws Exception {
		MemcachedFullCacheTransaction fullCacheTransaction = new MemcachedFullCacheTransaction(memcached, new OperationBean(OperationType.SAVE, bean));
		fullCacheTransaction.execute();
	}

	public void loadKey(Object bean) throws IllegalArgumentException, IllegalAccessException {
		String key = CacheUtils.getObjectKey(bean);
		memcached.add(INDEX_PREFIX + key, "");
	}

	public <T> T getById(Class<T> type, Object... params) {
		StringBuilder sb = new StringBuilder(256);
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

	public <T> T getById(AbstractDB db, boolean checkKey, int exp, Class<T> type, Object... params){
		StringBuilder sb = new StringBuilder(256);
		sb.append(type.getName());
		for (Object v : params) {
			sb.append(SPLIT);
			sb.append(v);
		}

		String key = sb.toString();
		byte[] data = memcached.getAndTocuh(key, exp);
		T t = null;
		if (data == null) {
			if (checkKey) {
				if (memcached.get(INDEX_PREFIX + key) != null) {
					t = (T) db.getByIdFromDB(type, null, params);
				}
			} else {
				t = (T) db.getByIdFromDB(type, null, params);
			}

			if (t != null) {
				memcached.add(key, exp, CacheUtils.encode(t));
				;
			}
		} else {
			t = CacheUtils.decode(type, data);
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getByIdList(AbstractDB db, Class<T> type, Object... params) {
		TableInfo tableInfo = AbstractDB.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length == params.length) {
			T t = getById(type, params);
			return t == null ? null : Arrays.asList(t);
		}

		if (tableInfo.getPrimaryKeyColumns().length == 1) {// 只有一个主键的表不缓存索引
			return db.getByIdListFromDB(type, null, params);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(type.getName());
		for (Object v : params) {
			sb.append(SPLIT);
			sb.append(v);
		}

		LinkedHashMap<String, Byte> map = memcached.get(sb.toString());
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, byte[]> cacheMap = memcached.get(map.keySet());
		List<T> list = new ArrayList<T>();
		for (Entry<String, Byte> entry : map.entrySet()) {
			byte[] data = cacheMap.get(entry.getKey());
			if (data == null) {
				continue;
			}

			list.add(CacheUtils.decode(type, data));
		}
		return list;
	}

	public <T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters) {
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
				value.put(keyMap.get(entry.getKey()), CacheUtils.decode(type, entry.getValue()));
			}
		}
		return value;
	}

	private String getObjectKey(Class<?> type, Object... params) {
		StringBuilder sb = new StringBuilder(256);
		sb.append(type.getName());
		for (Object v : params) {
			sb.append(SPLIT);
			sb.append(v);
		}
		return sb.toString();
	}

	public <T> PrimaryKeyValue<T> getById(AbstractDB db, boolean checkKey, Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		Map<String, PrimaryKeyParameter> keyMap = new HashMap<String, PrimaryKeyParameter>();
		for (PrimaryKeyParameter parameter : primaryKeyParameters) {
			keyMap.put(getObjectKey(type, parameter.getParams()), parameter);
		}

		PrimaryKeyValue<T> primaryKeyValue = new PrimaryKeyValue<T>();
		Map<String, byte[]> cacheMap = memcached.get(keyMap.keySet());
		if (cacheMap != null && !cacheMap.isEmpty()) {
			for (Entry<String, byte[]> entry : cacheMap.entrySet()) {
				primaryKeyValue.put(keyMap.get(entry.getKey()), CacheUtils.decode(type, entry.getValue()));
			}
		}

		if (cacheMap == null || cacheMap.size() != primaryKeyParameters.size()) {
			Map<String, String> notFindMap = new HashMap<String, String>();
			for (Entry<String, PrimaryKeyParameter> entry : keyMap.entrySet()) {
				if (cacheMap == null || cacheMap.containsKey(entry.getKey())) {
					continue;
				}

				notFindMap.put(entry.getKey(), INDEX_PREFIX + entry.getKey());
			}

			// key是有缓存的
			if (checkKey) {
				Map<String, byte[]> existMap = memcached.get(notFindMap.values());
				if (existMap != null) {
					Iterator<Entry<String, String>> iterator = notFindMap.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<String, String> entry = iterator.next();
						if (!existMap.containsKey(entry.getValue())) {
							iterator.remove();
						}
					}
				}
			}

			List<PrimaryKeyParameter> list = new ArrayList<PrimaryKeyParameter>();
			for (Entry<String, String> entry : notFindMap.entrySet()) {
				list.add(keyMap.get(entry.getKey()));
			}

			PrimaryKeyValue<T> dbMap = db.getByIdFromDB(type, null, list);
			if (dbMap != null) {
				primaryKeyValue.putAll(dbMap);
				// saveToCacheByKeys(bean, exp);
			}
		}
		return primaryKeyValue;
	}

	public void opByFull(OperationBean operationBean) throws Exception {
		Transaction transaction = new MemcachedFullCacheTransaction(memcached, operationBean);
		TransactionContext.getInstance().execute(Arrays.asList(transaction));
	}

	public void opHotspot(OperationBean operationBean, int exp, boolean keys) throws Exception {
		Transaction transaction = new MemcachedHotspotCacheTransaction(memcached, exp, keys, operationBean);
		TransactionContext.getInstance().execute(Arrays.asList(transaction));
	}

	public void hostspotDataAsyncRollback(OperationBean operationBean, boolean keys, boolean exist) throws IllegalArgumentException, IllegalAccessException {
		String key = CacheUtils.getObjectKey(operationBean.getBean());
		memcached.delete(key);
		if(keys){
			if(exist){
				memcached.add(Cache.INDEX_PREFIX + key, "");
			}else{
				memcached.delete(Cache.INDEX_PREFIX + key);
			}
		}
	}
}
