package shuchaowen.db.storage.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.common.transaction.Transaction;
import shuchaowen.db.AbstractDB;
import shuchaowen.db.OperationBean;
import shuchaowen.db.OperationType;
import shuchaowen.db.TableInfo;
import shuchaowen.db.storage.CacheUtils;
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

	public Transaction opByFull(OperationBean operationBean) throws Exception {
		return new MemcachedFullCacheTransaction(memcached, operationBean);
	}

	public Transaction opHotspot(OperationBean operationBean, int exp, boolean keys) throws Exception {
		return new MemcachedHotspotCacheTransaction(memcached, exp, keys, operationBean);
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
