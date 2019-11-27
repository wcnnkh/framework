package scw.db.cache;

import java.util.Arrays;

import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.data.Cache;
import scw.data.TransactionContextCache;
import scw.data.WrapperCache;
import scw.orm.sql.SqlMappingOperations;

public final class DefaultCacheManager extends AbstractCacheManager<Cache> {
	private final Cache cache;
	private final SqlMappingOperations sqlMappingOperations;

	/**
	 * 过期时间由cache实现
	 * 
	 * @param cache
	 * @param transaction
	 *            是否开启事务， 如果开启在处理失败后会删除key
	 * @param keyPrefix
	 */
	public DefaultCacheManager(SqlMappingOperations sqlMappingOperations, Cache cache, boolean transaction,
			String keyPrefix) {
		this.cache = new WrapperCache(cache, transaction, keyPrefix);
		this.sqlMappingOperations = sqlMappingOperations;
	}

	public DefaultCacheManager(SqlMappingOperations sqlMappingOperations) {
		this.cache = new TransactionContextCache(this);
		this.sqlMappingOperations = sqlMappingOperations;
	}

	@Override
	public SqlMappingOperations getSqlMappingOperations() {
		return sqlMappingOperations;
	}

	public void save(Object bean) {
		cache.add(sqlMappingOperations.getObjectKey(ClassUtils.getUserClass(bean), bean), bean);
	}

	public void update(Object bean) {
		cache.set(sqlMappingOperations.getObjectKey(ClassUtils.getUserClass(bean), bean), bean);
	}

	public void saveOrUpdate(Object bean) {
		update(bean);
	}

	public <T> T getById(Class<T> type, Object... params) {
		if (ArrayUtils.isEmpty(params)) {
			return null;
		}

		return cache.get(sqlMappingOperations.getObjectKeyById(type, Arrays.asList(params)));
	}

	@Override
	public Cache getCache() {
		return cache;
	}

	public void deleteById(Class<?> type, Object... params) {
		if (ArrayUtils.isEmpty(params)) {
			return;
		}

		getCache().delete(sqlMappingOperations.getObjectKeyById(type, Arrays.asList(params)));
	}

	public void delete(Object bean) {
		getCache().delete(sqlMappingOperations.getObjectKey(ClassUtils.getUserClass(bean), bean));
	}
}
