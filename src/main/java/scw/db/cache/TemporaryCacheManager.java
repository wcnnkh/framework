package scw.db.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.XTime;
import scw.data.TemporaryCache;
import scw.data.WrapperTemporaryCache;
import scw.orm.sql.SqlMappingOperations;
import scw.orm.sql.TableMappingContext;

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
	private final SqlMappingOperations sqlMappingOperations;

	public TemporaryCacheManager(SqlMappingOperations sqlMappingOperations, TemporaryCache cache, boolean transaction,
			String keyPrefix) {
		this.cache = new WrapperTemporaryCache(cache, transaction, keyPrefix);
		this.sqlMappingOperations = sqlMappingOperations;
	}

	@Override
	public SqlMappingOperations getSqlMappingOperations() {
		return sqlMappingOperations;
	}

	public void save(Object bean) {
		Class<?> clazz = ClassUtils.getUserClass(bean);
		TemporaryCacheConfig config = getCacheConfig(clazz);
		if (!config.isEnable()) {
			return;
		}

		String objectKey = sqlMappingOperations.getObjectKey(clazz, bean);
		cache.set(objectKey, config.getExp(), bean);
		if (config.isKeys()) {
			cache.add(KEY + objectKey, "");
		}
	}

	public void update(Object bean) {
		Class<?> clazz = ClassUtils.getUserClass(bean);
		TemporaryCacheConfig config = getCacheConfig(clazz);
		if (!config.isEnable()) {
			return;
		}

		cache.set(sqlMappingOperations.getObjectKey(clazz, bean), config.getExp(), bean);
	}

	public void delete(Object bean) {
		Class<?> clazz = ClassUtils.getUserClass(bean);
		TemporaryCacheConfig config = getCacheConfig(clazz);
		if (!config.isEnable()) {
			return;
		}

		String objectKey = sqlMappingOperations.getObjectKey(clazz, bean);
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

		String objectKey = sqlMappingOperations.getObjectKeyById(type, Arrays.asList(params));
		if (config.isKeys()) {
			cache.delete(KEY + objectKey);
		}
		cache.delete(objectKey);
	}

	public void saveOrUpdate(Object bean) {
		Class<?> clazz = ClassUtils.getUserClass(bean);
		TemporaryCacheConfig config = getCacheConfig(clazz);
		if (!config.isEnable()) {
			return;
		}

		String objectKey = sqlMappingOperations.getObjectKey(clazz, bean);
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

		return cache.getAndTouch(sqlMappingOperations.getObjectKeyById(type, Arrays.asList(params)), config.getExp());
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
			TableMappingContext tableFieldContext = sqlMappingOperations.getTableMappingContext(type);
			if (tableFieldContext.getPrimaryKeys().size() != params.length) {
				return true;
			}

			String key = sqlMappingOperations.getObjectKeyById(type, Arrays.asList(params));
			return getCache().isExist(KEY + key);
		}
		return true;
	}
}
