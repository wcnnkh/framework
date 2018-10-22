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

import shuchaowen.core.cache.CAS;
import shuchaowen.core.cache.Memcached;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.storage.CacheUtils;

public class MemcachedCache implements Cache{
	private static final String SPLIT = "#";
	private static final String INDEX_PREFIX = "index#";
	private final Memcached memcached;
	
	public MemcachedCache(Memcached memcached){
		this.memcached = memcached;
	}
	
	private void casSaveIndex(String indexKey, String objectKey) {
		boolean b = false;
		while (!b) {
			CAS<LinkedHashMap<String, Byte>> map = memcached.gets(indexKey);
			if (map == null || map.getValue() == null) {
				LinkedHashMap<String, Byte> valueMap = new LinkedHashMap<String, Byte>();
				valueMap.putIfAbsent(objectKey, (byte) 0);
				b = memcached.add(indexKey, valueMap);
			} else {
				map.getValue().putIfAbsent(objectKey, (byte) 0);
				b = memcached.cas(indexKey, map.getValue(), map.getCas());
			}
		}
	}

	private void casDeleteIndex(String indexKey, String objectKey) {
		boolean b = false;
		while (!b) {
			CAS<LinkedHashMap<String, byte[]>> map = memcached.get(indexKey);
			if (map != null && map.getValue() != null) {
				map.getValue().remove(objectKey);
			}
			b = memcached.cas(indexKey, map.getValue(), map.getCas());
		}
	}
	
	public void saveBeanAndIndex(Object bean) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		Object v = tableInfo.getPrimaryKeyColumns()[0].getFieldInfo().forceGet(bean);
		sb.append(SPLIT);
		sb.append(v);
		
		String objectKey;
		if(tableInfo.getPrimaryKeyColumns().length > 1){
			String[] indexKeys = new String[tableInfo.getPrimaryKeyColumns().length - 1];
			for(int i=1; i<tableInfo.getPrimaryKeyColumns().length; i++){
				if(i <= indexKeys.length){
					indexKeys[i - 1] = sb.toString();
				}
				
				v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
				sb.append(SPLIT);
				sb.append(v);
			}
			
			objectKey = sb.toString();
			for(String indexKey : indexKeys){
				casSaveIndex(indexKey, objectKey);
			}
		}else{
			objectKey = sb.toString();
		}
		memcached.add(objectKey, CacheUtils.encode(bean));
	}

	public void updateBeanAndIndex(Object bean) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		memcached.set(sb.toString(), CacheUtils.encode(bean));
	}

	public void deleteBeanAndIndex(Object bean) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		Object v = tableInfo.getPrimaryKeyColumns()[0].getFieldInfo().forceGet(bean);
		sb.append(SPLIT);
		sb.append(v);
		
		String objectKey;
		if(tableInfo.getPrimaryKeyColumns().length > 1){
			String[] indexKeys = new String[tableInfo.getPrimaryKeyColumns().length - 1];
			for(int i=1; i<tableInfo.getPrimaryKeyColumns().length; i++){
				if(i <= indexKeys.length){
					indexKeys[i - 1] = sb.toString();
				}
				
				v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
				sb.append(SPLIT);
				sb.append(v);
			}
			
			objectKey = sb.toString();
			for(String indexKey : indexKeys){
				casDeleteIndex(indexKey, objectKey);
			}
		}else{
			objectKey = sb.toString();
		}
		memcached.delete(sb.toString());
	}

	public void saveOrUpdateBeanAndIndex(Object bean) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		Object v = tableInfo.getPrimaryKeyColumns()[0].getFieldInfo().forceGet(bean);
		sb.append(SPLIT);
		sb.append(v);
		
		String objectKey;
		if(tableInfo.getPrimaryKeyColumns().length > 1){
			String[] indexKeys = new String[tableInfo.getPrimaryKeyColumns().length - 1];
			for(int i=1; i<tableInfo.getPrimaryKeyColumns().length; i++){
				if(i <= indexKeys.length){
					indexKeys[i - 1] = sb.toString();
				}
				
				v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
				sb.append(SPLIT);
				sb.append(v);
			}
			
			objectKey = sb.toString();
			for(String indexKey : indexKeys){
				casSaveIndex(indexKey, objectKey);
			}
		}else{
			objectKey = sb.toString();
		}
		memcached.set(objectKey, CacheUtils.encode(bean));
	}

	public void saveBean(Object bean, int exp) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
			sb.append(SPLIT);
			sb.append(v);
		}
		memcached.add(sb.toString(), exp, CacheUtils.encode(bean));
	}

	public void updateBean(Object bean, int exp) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		memcached.set(sb.toString(), exp, CacheUtils.encode(bean));
	}

	public void deleteBean(Object bean) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
			sb.append(SPLIT);
			sb.append(v);
		}
		memcached.delete(sb.toString());
	}

	public void saveOrUpdateBean(Object bean, int exp) throws IllegalArgumentException, IllegalAccessException {
		updateBean(bean, exp);
	}

	public void saveBeanAndKey(Object bean, int exp) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
			sb.append(SPLIT);
			sb.append(v);
		}
		String key = sb.toString();

		memcached.add(INDEX_PREFIX + key, "");
		memcached.add(key, exp, CacheUtils.encode(bean));
	}

	public void updateBeanAndKey(Object bean, int exp) throws IllegalArgumentException, IllegalAccessException {
		updateBean(bean, exp);
	}

	public void deleteBeanAndKey(Object bean) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
			sb.append(SPLIT);
			sb.append(v);
		}

		String key = sb.toString();
		memcached.delete(INDEX_PREFIX + key);
		memcached.delete(key);
	}

	public void saveOrUpdateBeanAndKey(Object bean, int exp) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
			sb.append(SPLIT);
			sb.append(v);
		}

		String key = sb.toString();
		memcached.add(INDEX_PREFIX + key, "");
		memcached.set(key, exp, CacheUtils.encode(bean));
	}

	public void loadBeanAndIndex(Object bean) throws IllegalArgumentException, IllegalAccessException {
		saveBeanAndIndex(bean);
	}

	public void loadBeanAndKey(Object bean) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
			sb.append(SPLIT);
			sb.append(v);
		}

		String key = sb.toString();
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

	public <T> T getById(AbstractDB db, boolean checkKey, int exp, Class<T> type, Object... params) throws IllegalArgumentException, IllegalAccessException {
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
					if (t != null) {
						saveBeanAndKey(t, exp);
					}
				}
			} else {
				t = (T) db.getByIdFromDB(type, null, params);
				if (t != null) {
					saveBean(t, exp);
				}
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
				value.put(keyMap.get(entry.getKey()),
						CacheUtils.decode(type, entry.getValue()));
			}
		}
		return value;
	}
	
	private String getObjectKey(Class<?> type, Object ...params){
		StringBuilder sb = new StringBuilder(256);
		sb.append(type.getName());
		for(Object v : params){
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
			if(checkKey){
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
				//saveToCacheByKeys(bean, exp);
			}
		}
		return primaryKeyValue;
	}

}
