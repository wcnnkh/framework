package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.ArrayUtils;
import scw.core.utils.XTime;
import scw.data.TemporaryCache;
import scw.data.WrapperTemporaryCache;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public final class TemporaryCacheManager extends AbstractCacheManager<TemporaryCache> {
	private static final String KEY = "key:";
	private static final TemporaryCacheConfig DEFAULT_CONFIG = new TemporaryCacheConfig(
			(int) (XTime.ONE_DAY * 2 / 1000), false, true);
	private static volatile Map<Class<?>, TemporaryCacheConfig> configMap = new HashMap<Class<?>, TemporaryCacheConfig>();

	private static final TemporaryCacheConfig getCacheConfig(Class<?> tableClass) {
		TemporaryCacheConfig config = configMap.get(tableClass);
		if (config == null) {
			synchronized (configMap) {
				config = configMap.get(tableClass);
				if (config == null) {
					TemporaryCacheEnable temporaryCacheEnable = tableClass.getAnnotation(TemporaryCacheEnable.class);
					if (temporaryCacheEnable == null) {
						config = DEFAULT_CONFIG;
					} else {
						config = new TemporaryCacheConfig(temporaryCacheEnable);
					}
					configMap.put(tableClass, config);
				}
			}
		}
		return config;
	}

	private final TemporaryCache cache;

	public TemporaryCacheManager(TemporaryCache cache, boolean transaction, String keyPrefix) {
		this.cache = new WrapperTemporaryCache(cache, transaction, keyPrefix);
	}

	public void save(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		TemporaryCacheConfig config = getCacheConfig(tableInfo.getSource());
		if (!config.isEnable()) {
			return;
		}

		String objectKey = ORMUtils.getObjectKey(tableInfo, bean);
		cache.set(objectKey, config.getExp(), bean);
		if (config.isKeys()) {
			cache.add(KEY + objectKey, "");
		}
	}

	public void update(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		TemporaryCacheConfig config = getCacheConfig(tableInfo.getSource());
		if (!config.isEnable()) {
			return;
		}

		cache.set(ORMUtils.getObjectKey(tableInfo, bean), config.getExp(), bean);
	}

	public void delete(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		TemporaryCacheConfig config = getCacheConfig(tableInfo.getSource());
		if (!config.isEnable()) {
			return;
		}

		final String objectKey = ORMUtils.getObjectKey(tableInfo, bean);
		if (config.isKeys()) {
			cache.delete(KEY + objectKey);
		}
		cache.delete(objectKey);
	}

	public void deleteById(Class<?> type, Object... params) {
		TemporaryCacheConfig config = getCacheConfig(type);
		if (!config.isEnable()) {
			return;
		}

		final String objectKey = ORMUtils.getObjectKeyById(type, params);
		if (config.isKeys()) {
			cache.delete(KEY + objectKey);
		}
		cache.delete(objectKey);
	}

	public void saveOrUpdate(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		TemporaryCacheConfig config = getCacheConfig(tableInfo.getSource());
		if (!config.isEnable()) {
			return;
		}

		final String objectKey = ORMUtils.getObjectKey(tableInfo, bean);
		cache.set(objectKey, config.getExp(), bean);
		if (config.isKeys()) {
			cache.set(KEY + objectKey, "");
		}
	}

	public <T> T getById(Class<T> type, Object... params) {
		TemporaryCacheConfig config = getCacheConfig(type);
		if (!config.isEnable()) {
			return null;
		}

		return cache.getAndTouch(ORMUtils.getObjectKeyById(type, params), config.getExp());
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) {
		TemporaryCacheConfig config = getCacheConfig(type);
		if (!config.isEnable()) {
			return null;
		}

		return super.getInIdList(type, inIds, params);
	}

	@Override
	public TemporaryCache getCache() {
		return cache;
	}

	@Override
	public boolean isSearchDB(Class<?> type, Object... params) {
		if (ArrayUtils.isEmpty(params)) {
			return false;
		}

		TemporaryCacheConfig config = getCacheConfig(type);
		if (config.isEnable() && config.isKeys()) {
			TableInfo tableInfo = ORMUtils.getTableInfo(type);
			if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length != params.length) {
				return false;
			}
			
			String key = ORMUtils.getObjectKeyById(type, params);
			return getCache().isExist(KEY + key);
		}
		return true;
	}
}
