package scw.sql.orm.cache;

import scw.sql.orm.AbstractORMTemplate;
import scw.sql.orm.SqlFormat;

public abstract class AbstractORMCacheTemplate extends AbstractORMTemplate {
	private final Cache cache;

	public AbstractORMCacheTemplate(SqlFormat sqlFormat, Cache cache) {
		super(sqlFormat);
		this.cache = cache;
	}

	@Override
	public <T> T getById(String tableName, Class<T> type, Object... params) {
		if (cache == null) {
			return super.getById(tableName, type, params);
		}

		String cacheKey = CacheUtils.getByIdCacheKey(type, params);
		T t = cache.get(type, cacheKey);
		if (t == null) {
			t = super.getById(tableName, type, params);
			if (t != null) {
				cache.set(cacheKey, t);
			}
		}
		return t;
	}

	@Override
	public boolean delete(Object bean, String tableName) {
		if (cache == null) {
			return super.delete(bean, tableName);
		}

		cache.delete(CacheUtils.getObjectCacheKey(bean));
		return super.delete(bean, tableName);
	}

	@Override
	public boolean delete(Object bean) {
		if (cache == null) {
			return super.delete(bean);
		}

		cache.delete(CacheUtils.getObjectCacheKey(bean));
		return super.delete(bean);
	}

	@Override
	public boolean deleteById(Class<?> type, Object... params) {
		if (cache == null) {
			return super.deleteById(type, params);
		}

		cache.delete(CacheUtils.getByIdCacheKey(type, params));
		return super.deleteById(type, params);
	}

	@Override
	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		if (cache == null) {
			return super.deleteById(tableName, type, params);
		}

		cache.delete(CacheUtils.getByIdCacheKey(type, params));
		return super.deleteById(tableName, type, params);
	}

	@Override
	public boolean save(Object bean, String tableName) {
		if (cache == null) {
			return super.save(bean, tableName);
		}

		cache.add(CacheUtils.getObjectCacheKey(bean), bean);
		return super.save(bean, tableName);
	}

	@Override
	public boolean save(Object bean) {
		if (cache == null) {
			return super.save(bean);
		}

		cache.add(CacheUtils.getObjectCacheKey(bean), bean);
		return super.save(bean);
	}

	@Override
	public boolean saveOrUpdate(Object bean) {
		if (cache == null) {
			return super.saveOrUpdate(bean);
		}

		cache.set(CacheUtils.getObjectCacheKey(bean), bean);
		return super.saveOrUpdate(bean);
	}

	@Override
	public boolean saveOrUpdate(Object bean, String tableName) {
		if (cache == null) {
			return super.saveOrUpdate(bean, tableName);
		}

		cache.set(CacheUtils.getObjectCacheKey(bean), bean);
		return super.saveOrUpdate(bean, tableName);
	}

	@Override
	public boolean update(Object bean) {
		if (cache == null) {
			return super.update(bean);
		}

		cache.set(CacheUtils.getObjectCacheKey(bean), bean);
		return super.update(bean);
	}

	@Override
	public boolean update(Object bean, String tableName) {
		if (cache == null) {
			return super.update(bean, tableName);
		}

		cache.set(CacheUtils.getObjectCacheKey(bean), bean);
		return super.update(bean, tableName);
	}
}
