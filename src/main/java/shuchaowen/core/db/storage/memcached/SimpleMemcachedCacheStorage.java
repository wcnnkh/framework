package shuchaowen.core.db.storage.memcached;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.cache.Memcached;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.db.storage.CommonStorage;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.XTime;

public class SimpleMemcachedCacheStorage extends CommonStorage{
	private static final int DEFAULT_EXP = (int) ((7 * XTime.ONE_DAY) / 1000);
	private HashSet<String> excludeMap = new HashSet<String>();
	private final Memcached memcached;
	
	public SimpleMemcachedCacheStorage(AbstractDB db, Memcached memcached) {
		super(db, null, null);
		this.memcached = memcached;
	}

	public Memcached getMemcached() {
		return memcached;
	}
	
	public void excludeTable(Class<?> ...tableClass){
		for(Class<?> t : tableClass){
			excludeMap.add(ClassUtils.getCGLIBRealClassName(t));
		}
	}
	
	@Override
	public <T> T getById(Class<T> type, Object... params) {
		if(excludeMap.contains(ClassUtils.getCGLIBRealClassName(type))){
			return super.getById(type, params);
		}
		
		String key = CacheUtils.getObjectKey(type, params);
		byte[] data = memcached.getAndTocuh(key, DEFAULT_EXP);
		if(data != null){
			return CacheUtils.decode(type, data);
		}
		
		T t = super.getById(type, params);
		if(t == null){
			return t;
		}
		memcached.set(key, DEFAULT_EXP, CacheUtils.encode(t));
		return t;
	}
	
	@Override
	public <T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters) {
		if(excludeMap.contains(ClassUtils.getCGLIBRealClassName(type))){
			return super.getById(type, primaryKeyParameters);
		}
		
		Map<String, PrimaryKeyParameter> keyMap = new HashMap<String, PrimaryKeyParameter>();
		for (PrimaryKeyParameter parameter : primaryKeyParameters) {
			keyMap.put(CacheUtils.getObjectKey(type, parameter), parameter);
		}

		PrimaryKeyValue<T> primaryKeyValue = new PrimaryKeyValue<T>();
		Map<String, byte[]> cacheMap = memcached.get(keyMap.keySet());
		if(cacheMap != null && cacheMap.size() == primaryKeyParameters.size()){
			for (Entry<String, byte[]> entry : cacheMap.entrySet()) {
				primaryKeyValue.put(keyMap.get(entry.getKey()), CacheUtils.decode(type, entry.getValue()));
			}
		}else{
			if(cacheMap != null){
				for (Entry<String, byte[]> entry : cacheMap.entrySet()) {
					primaryKeyValue.put(keyMap.get(entry.getKey()), CacheUtils.decode(type, entry.getValue()));
					keyMap.remove(entry.getKey());
				}
			}
			
			PrimaryKeyValue<T> dbValue = super.getById(type, keyMap.values());
			if(dbValue != null){
				primaryKeyValue.putAll(dbValue);
				for(Object v : dbValue.values()){
					try {
						memcached.add(CacheUtils.getObjectKey(v), CacheUtils.encode(v));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return primaryKeyValue;
	}
	
	@Override
	public void save(Collection<?> beans) {
		for(Object bean : beans){
			if(excludeMap.contains(ClassUtils.getCGLIBRealClassName(beans.getClass()))){
				continue;
			}
			
			try {
				memcached.add(CacheUtils.getObjectKey(bean), DEFAULT_EXP, CacheUtils.encode(bean));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		super.save(beans);
	}
	
	@Override
	public void delete(Collection<?> beans) {
		for(Object bean : beans){
			if(excludeMap.contains(ClassUtils.getCGLIBRealClassName(beans.getClass()))){
				continue;
			}
			
			try {
				memcached.delete(CacheUtils.getObjectKey(bean));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		super.delete(beans);
	}
	
	@Override
	public void update(Collection<?> beans) {
		for(Object bean : beans){
			if(excludeMap.contains(ClassUtils.getCGLIBRealClassName(beans.getClass()))){
				continue;
			}
			
			try {
				memcached.set(CacheUtils.getObjectKey(bean), DEFAULT_EXP, CacheUtils.encode(bean));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		super.update(beans);
	}
	
	@Override
	public void saveOrUpdate(Collection<?> beans) {
		for(Object bean : beans){
			if(excludeMap.contains(ClassUtils.getCGLIBRealClassName(beans.getClass()))){
				continue;
			}
			
			try {
				memcached.set(CacheUtils.getObjectKey(bean), DEFAULT_EXP, CacheUtils.encode(bean));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		super.saveOrUpdate(beans);
	}
}
