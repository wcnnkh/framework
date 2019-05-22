package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.utils.XTime;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;

public abstract class LazyDataManager implements CacheManager {
	private static final String DEFAULT_KEY_PREFIX = "lazy:";
	private static final String KEY = "key:";
	private static final String DEFAULT_CONNECTOR = "|";
	private static final LazyCacheConfig DEFAULT_CONFIG = new DefaultLazyCacheConfig((int) (XTime.ONE_DAY * 2 / 1000),
			false, false);
	private static volatile Map<Class<?>, LazyCacheConfig> configMap = new HashMap<Class<?>, LazyCacheConfig>();

	protected LazyCacheConfig getCacheConfig(Class<?> tableClass) {
		LazyCacheConfig config = configMap.get(tableClass);
		if (config == null) {
			synchronized (configMap) {
				config = configMap.get(tableClass);
				if (config == null) {
					LazyCache lazyCache = tableClass.getAnnotation(LazyCache.class);
					if (lazyCache == null) {
						config = DEFAULT_CONFIG;
					} else {
						config = new DefaultLazyCacheConfig(lazyCache);
					}
					configMap.put(tableClass, config);
				}
			}
		}
		return config;
	}

	protected abstract void set(String key, int exp, Object value);

	protected abstract void del(String key);

	protected abstract <T> T getAndTouch(Class<T> type, String key, int exp);

	protected abstract <T> Map<String, T> mget(Class<T> type, Collection<String> keys);

	protected abstract void addKey(String key);

	protected abstract boolean isExist(String key);

	public void save(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		LazyCacheConfig config = getCacheConfig(tableInfo.getSource());
		if (config.isDisable()) {
			return;
		}

		final String objectKey = getObjectKey(tableInfo, bean);
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterRollback() {
				del(objectKey);
			}
		});
		set(objectKey, config.getExp(), bean);
		if (config.isKeys()) {
			final String index = KEY + objectKey;
			addKey(index);
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
				@Override
				public void afterRollback() {
					del(index);
				}
			});
		}
	}

	public void update(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		LazyCacheConfig config = getCacheConfig(tableInfo.getSource());
		if (config.isDisable()) {
			return;
		}

		final String objectKey = getObjectKey(tableInfo, bean);
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterRollback() {
				del(objectKey);
			}
		});
		set(objectKey, config.getExp(), bean);
	}

	public void delete(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		LazyCacheConfig config = getCacheConfig(tableInfo.getSource());
		if (config.isDisable()) {
			return;
		}

		final String objectKey = getObjectKey(tableInfo, bean);
		del(objectKey);
		if (config.isKeys()) {
			final String index = KEY + objectKey;
			del(index);
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
				@Override
				public void afterRollback() {
					addKey(index);
				}
			});
		}
	}

	public void deleteById(Class<?> type, Object... params) {
		LazyCacheConfig config = getCacheConfig(type);
		if (config.isDisable()) {
			return;
		}

		final String objectKey = getObjectKeyById(type, params);
		del(objectKey);
		if (config.isKeys()) {
			final String index = KEY + objectKey;
			del(index);
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
				@Override
				public void afterRollback() {
					addKey(index);
				}
			});
		}
	}

	public void saveOrUpdate(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		LazyCacheConfig config = getCacheConfig(tableInfo.getSource());
		if (config.isDisable()) {
			return;
		}

		final String objectKey = getObjectKey(tableInfo, bean);
		TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
			@Override
			public void afterRollback() {
				del(objectKey);
			}
		});
		set(objectKey, config.getExp(), bean);
		if (config.isKeys()) {
			final String index = KEY + objectKey;
			boolean exist = isExist(index);
			addKey(index);
			if (!exist) {
				TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
					@Override
					public void afterRollback() {
						del(index);
					}
				});
			}
		}
	}

	public <T> T getById(Class<T> type, Object... params) {
		LazyCacheConfig config = getCacheConfig(type);
		if (config.isDisable()) {
			return null;
		}

		return getAndTouch(type, getObjectKeyById(type, params), config.getExp());
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return null;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) {
		LazyCacheConfig config = getCacheConfig(type);
		if (config.isDisable()) {
			return null;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (params.length != tableInfo.getPrimaryKeyColumns().length - 1) {
			return null;
		}

		String key = getObjectKeyById(type, params);
		Map<String, K> keyMap = new HashMap<String, K>(inIds.size(), 1);
		for (K k : inIds) {
			keyMap.put(appendObjectKey(key, k), k);
		}

		Map<String, V> map = mget(type, keyMap.keySet());
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<K, V> valueMap = new HashMap<K, V>(map.size(), 1);
		for (Entry<String, V> entry : map.entrySet()) {
			K k = keyMap.get(entry.getKey());
			if (k == null) {
				continue;
			}

			valueMap.put(k, entry.getValue());
		}
		return valueMap;
	}

	public boolean isExist(Class<?> type, Object... params) {
		LazyCacheConfig config = getCacheConfig(type);
		if (config.isDisable()) {
			return true;
		}
		if (config.isKeys()) {
			return isExist(getObjectKeyById(type, params));
		}
		return true;
	}

	protected String getObjectKey(TableInfo tableInfo, Object bean) {
		ColumnInfo[] cs = tableInfo.getPrimaryKeyColumns();
		StringBuilder sb = new StringBuilder(128);
		sb.append(DEFAULT_KEY_PREFIX);
		sb.append(tableInfo.getSource().getName());
		sb.append(DEFAULT_CONNECTOR).append(cs.length);
		Object v;
		String value;
		try {
			for (int i = 0; i < cs.length; i++) {
				sb.append(DEFAULT_CONNECTOR);
				v = cs[i].getField().get(bean);
				value = v == null ? null : v.toString();
				sb.append(value == null ? 0 : value.length());
				sb.append(DEFAULT_CONNECTOR);
				sb.append(value);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	protected String getObjectKeyById(Class<?> clazz, Object... params) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(DEFAULT_KEY_PREFIX).append(clazz.getName());
		sb.append(DEFAULT_CONNECTOR).append(params.length);
		for (int i = 0; i < params.length; i++) {
			sb.append(DEFAULT_CONNECTOR);
			Object v = params[i];
			String value = v == null ? null : v.toString();
			sb.append(value == null ? 0 : value.length());
			sb.append(DEFAULT_CONNECTOR);
			sb.append(value);
		}
		return sb.toString();
	}

	protected String appendObjectKey(String key, Object value) {
		String v = value == null ? null : value.toString();
		int len = v == null ? 0 : v.length();
		StringBuilder sb = new StringBuilder(key.length() + len + 10);
		sb.append(key);
		sb.append(DEFAULT_CONNECTOR);
		sb.append(len);
		sb.append(DEFAULT_CONNECTOR);
		sb.append(v);
		return sb.toString();
	}
}
