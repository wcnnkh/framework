package shuchaowen.core.db.storage;

import java.util.Arrays;
import java.util.Collection;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public abstract class AbstractHotSpotDataCacheStorage extends DefaultStorage{
	private String prefix;
	private int exp;// 过期时间

	public AbstractHotSpotDataCacheStorage(String prefix, int exp) {
		this.prefix = prefix;
		this.exp = exp;
	}

	public abstract <T> T getAndTouch(Class<T> type, String key, int exp) throws Exception;

	public abstract void set(String key, int exp, Object data) throws Exception;

	public abstract boolean add(String key, int exp, Object data) throws Exception;

	public abstract boolean delete(String key) throws Exception;

	public <T> T getByIdToCache(Class<T> type, Object... params) {
		try {
			return getAndTouch(type, prefix + CacheUtils.getObjectKey(type, params), exp);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public void saveToCache(Collection<Object> beans) {
		for (Object bean : beans) {
			try {
				add(prefix + CacheUtils.getObjectKey(bean), exp, bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteToCache(Collection<Object> beans) {
		for (Object bean : beans) {
			try {
				delete(prefix + CacheUtils.getObjectKey(bean));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getById(AbstractDB db,
			SQLFormat sqlFormat, Class<T> type, Object... params) {
		Object t = getByIdToCache(type, params);
		if(t != null){
			return (T) t;
		}
		
		t = super.getById(db, sqlFormat, type, params);
		if(t != null){
			saveToCache(Arrays.asList(t));
		}
		return (T) t;
	}
	
	@Override
	public void save(Collection<Object> beans, AbstractDB db, SQLFormat sqlFormat) {
		deleteToCache(beans);
		super.save(beans, db, sqlFormat);
	}
	
	@Override
	public void delete(Collection<Object> beans,
			AbstractDB db, SQLFormat sqlFormat) {
		deleteToCache(beans);
		super.delete(beans, db, sqlFormat);
	}
	
	@Override
	public void update(Collection<Object> beans,
			AbstractDB db, SQLFormat sqlFormat) {
		deleteToCache(beans);
		super.update(beans, db, sqlFormat);
	}
	
	@Override
	public void saveOrUpdate(Collection<Object> beans,
			AbstractDB db, SQLFormat sqlFormat) {
		deleteToCache(beans);
		super.saveOrUpdate(beans, db, sqlFormat);
	}
}
