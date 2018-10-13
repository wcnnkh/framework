package shuchaowen.core.db.storage;

import java.util.Arrays;
import java.util.Collection;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.cache.Cache;

public abstract class AbstractCacheStorage extends CommonStorage {
	private final String prefix;
	private final int exp;// 过期时间
	private final Cache cache;

	public AbstractCacheStorage(AbstractDB db, Cache cache, String prefix, int exp, Storage execute) {
		super(db, null, execute);
		this.cache = cache;
		this.prefix = prefix;
		this.exp = exp;
	}

	private String getObjectKey(Object bean) {
		try {
			return CacheUtils.getObjectKey(bean);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> T getByIdToCache(Class<T> type, Object... params) {
		return cache.getAndTouch(type,
				prefix + CacheUtils.getObjectKey(type, params), exp);
	}

	public void saveToCache(Collection<Object> beans) {
		for (Object bean : beans) {
			cache.add(getObjectKey(bean), exp, bean);
		}
	}

	public void updateToCache(Collection<Object> beans) {
		for (Object bean : beans) {
			cache.set(prefix + getObjectKey(bean), exp, bean);
		}
	}

	public void deleteToCache(Collection<Object> beans) {
		for (Object bean : beans) {
			cache.delete(prefix + getObjectKey(bean));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getById(Class<T> type, Object... params) {
		Object t = getByIdToCache(type, params);
		if (t != null) {
			return (T) t;
		}

		t = super.getById(type, params);
		if (t != null) {
			saveToCache(Arrays.asList(t));
		}
		return (T) t;
	}

	@Override
	public void save(Collection<Object> beans) {
		super.save(beans);
		saveToCache(beans);
	}

	@Override
	public void delete(Collection<Object> beans) {
		super.delete(beans);
		deleteToCache(beans);
	}

	@Override
	public void update(Collection<Object> beans) {
		super.update(beans);
		updateToCache(beans);
	}

	@Override
	public void saveOrUpdate(Collection<Object> beans) {
		super.saveOrUpdate(beans);
		updateToCache(beans);
	}
}
