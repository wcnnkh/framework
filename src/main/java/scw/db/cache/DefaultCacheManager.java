package scw.db.cache;

import scw.core.utils.ArrayUtils;
import scw.data.Cache;
import scw.data.TransactionContextCache;
import scw.data.WrapperCache;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public class DefaultCacheManager extends AbstractCacheManager<Cache> {
	private final Cache cache;

	/**
	 * 过期时间由cache实现
	 * 
	 * @param cache
	 * @param transaction
	 *            是否开启事务， 如果开启在处理失败后会删除key
	 * @param keyPrefix
	 */
	public DefaultCacheManager(Cache cache, boolean transaction, String keyPrefix) {
		this.cache = new WrapperCache(cache, transaction, keyPrefix);
	}

	public DefaultCacheManager() {
		this.cache = TransactionContextCache.getInstance();
	}

	public void save(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length == 0) {
			return;
		}

		cache.add(ORMUtils.getObjectKey(tableInfo, bean), bean);
	}

	public void update(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length == 0) {
			return;
		}

		cache.set(ORMUtils.getObjectKey(tableInfo, bean), bean);
	}

	public void saveOrUpdate(Object bean) {
		update(bean);
	}

	public <T> T getById(Class<T> type, Object... params) {
		if (ArrayUtils.isEmpty(params)) {
			return null;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length != params.length) {
			return null;
		}

		return cache.get(ORMUtils.getObjectKeyById(type, params));
	}

	@Override
	public Cache getCache() {
		return cache;
	}

	public void deleteById(Class<?> type, Object... params) {
		if (ArrayUtils.isEmpty(params)) {
			return;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length != params.length) {
			return;
		}

		getCache().delete(ORMUtils.getObjectKeyById(tableInfo.getSource(), params));
	}

	public void delete(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length == 0) {
			return;
		}

		getCache().delete(ORMUtils.getObjectKey(tableInfo, bean));
	}
}
