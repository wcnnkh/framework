package scw.sql.orm.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.ArrayUtils;
import scw.core.utils.XTime;
import scw.data.TemporaryStorage;
import scw.data.TemporaryStorageWrapper;

public class TemporaryCacheManager extends AbstractCacheManager<TemporaryStorage> {
	private static final String KEY = "key:";
	private static final CacheConfig DEFAULT_CONFIG = new CacheConfig(
			(int) (XTime.ONE_DAY * 2 / 1000), false, true);
	private static final CacheConfig DISABLE = new CacheConfig(0, false, false);
	private static volatile Map<Class<?>, CacheConfig> configMap = new HashMap<Class<?>, CacheConfig>();

	private static final CacheConfig getCacheConfig(Class<?> tableClass) {
		if(!(tableClass instanceof Serializable)){
			return DISABLE;
		}
		
		CacheConfig config = configMap.get(tableClass);
		if (config == null) {
			synchronized (configMap) {
				config = configMap.get(tableClass);
				if (config == null) {
					CacheEnable temporaryCacheEnable = tableClass
							.getAnnotation(CacheEnable.class);
					if (temporaryCacheEnable == null) {
						config = DEFAULT_CONFIG;
					} else {
						config = new CacheConfig(temporaryCacheEnable);
					}
					configMap.put(tableClass, config);
				}
			}
		}
		return config;
	}

	private final TemporaryStorage cache;

	public TemporaryCacheManager(TemporaryStorage cache, boolean transaction,
			String keyPrefix) {
		this.cache = new TemporaryStorageWrapper(cache, transaction, keyPrefix);
	}

	public void save(Object bean) {
		Class<?> clazz = getUserClass(
				bean.getClass());
		CacheConfig config = getCacheConfig(clazz);
		if (!config.isEnable()) {
			return;
		}

		String objectKey = getObjectRelationalMapping().getObjectKey(clazz,
				bean);
		cache.set(objectKey, config.getExp(), bean);
		if (config.isKeys()) {
			cache.add(KEY + objectKey, "");
		}
	}

	public void update(Object bean) {
		Class<?> clazz = getUserClass(
				bean.getClass());
		CacheConfig config = getCacheConfig(clazz);
		if (!config.isEnable()) {
			return;
		}

		cache.set(getObjectRelationalMapping().getObjectKey(clazz, bean),
				config.getExp(), bean);
	}

	public void delete(Object bean) {
		Class<?> clazz = getUserClass(
				bean.getClass());
		CacheConfig config = getCacheConfig(clazz);
		if (!config.isEnable()) {
			return;
		}

		String objectKey = getObjectRelationalMapping().getObjectKey(clazz,
				bean);
		if (config.isKeys()) {
			cache.delete(KEY + objectKey);
		}
		cache.delete(objectKey);
	}

	public void deleteById(Class<?> type, Object... params) {
		CacheConfig config = getCacheConfig(type);
		if (!config.isEnable()) {
			return;
		}

		String objectKey = getObjectRelationalMapping().getObjectKeyById(type,
				Arrays.asList(params));
		if (config.isKeys()) {
			cache.delete(KEY + objectKey);
		}
		cache.delete(objectKey);
	}

	public void saveOrUpdate(Object bean) {
		Class<?> clazz = getUserClass(
				bean.getClass());
		CacheConfig config = getCacheConfig(clazz);
		if (!config.isEnable()) {
			return;
		}

		String objectKey = getObjectRelationalMapping().getObjectKey(clazz,
				bean);
		cache.set(objectKey, config.getExp(), bean);
		if (config.isKeys()) {
			cache.set(KEY + objectKey, "");
		}
	}

	public <T> T getById(Class<? extends T> type, Object... params) {
		CacheConfig config = getCacheConfig(type);
		if (!config.isEnable()) {
			return null;
		}

		return cache.getAndTouch(
				getObjectRelationalMapping().getObjectKeyById(type,
						Arrays.asList(params)), config.getExp());
	}

	public <K, V> Map<K, V> getInIdList(Class<? extends V> type,
			Collection<? extends K> inIds, Object... params) {
		CacheConfig config = getCacheConfig(type);
		if (!config.isEnable()) {
			return null;
		}

		return super.getInIdList(type, inIds, params);
	}

	@Override
	public TemporaryStorage getCache() {
		return cache;
	}

	@Override
	public boolean isSearchDB(Class<?> type, Object... params) {
		if (ArrayUtils.isEmpty(params)) {
			return false;
		}

		CacheConfig config = getCacheConfig(type);
		if (config.isEnable() && config.isKeys()) {
			if (getObjectRelationalMapping().getColumns(type).getPrimaryKeys().size() != params.length) {
				return true;
			}

			String key = getObjectRelationalMapping().getObjectKeyById(type,
					Arrays.asList(params));
			return getCache().isExist(KEY + key);
		}
		return true;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + cache + "]";
	}
}
