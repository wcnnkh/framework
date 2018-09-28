package shuchaowen.core.db.storage;

import java.util.Arrays;
import java.util.Collection;

import shuchaowen.core.db.ConnectionOrigin;
import shuchaowen.core.db.sql.format.SQLFormat;

public abstract class AbstractHotSpotDataCacheStorage extends DefaultStorage{
	public abstract <T> T getByIdToCache(Class<T> type, Object ...params);
	
	public abstract void saveToCache(Collection<Object> beans);
	
	public abstract void deleteToCache(Collection<Object> beans);
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getById(ConnectionOrigin connectionOrigin,
			SQLFormat sqlFormat, Class<T> type, Object... params) {
		Object t = getByIdToCache(type, params);
		if(t == null){
			t = super.getById(connectionOrigin, sqlFormat, type, params);
		}
		
		if(t != null){
			saveToCache(Arrays.asList(t));
		}
		return (T) t;
	}
	
	@Override
	public void save(Collection<Object> beans,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		deleteToCache(beans);
		super.save(beans, connectionOrigin, sqlFormat);
	}
	
	@Override
	public void delete(Collection<Object> beans,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		deleteToCache(beans);
		super.delete(beans, connectionOrigin, sqlFormat);
	}
	
	@Override
	public void update(Collection<Object> beans,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		deleteToCache(beans);
		super.update(beans, connectionOrigin, sqlFormat);
	}
	
	@Override
	public void saveOrUpdate(Collection<Object> beans,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		deleteToCache(beans);
		super.saveOrUpdate(beans, connectionOrigin, sqlFormat);
	}
	
	@Override
	public void incr(Object obj, String field, double limit, Double maxValue,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		deleteToCache(Arrays.asList(obj));
		super.incr(obj, field, limit, maxValue, connectionOrigin, sqlFormat);
	}
	
	@Override
	public void decr(Object obj, String field, double limit, Double minValue,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		deleteToCache(Arrays.asList(obj));
		super.decr(obj, field, limit, minValue, connectionOrigin, sqlFormat);
	}
}
