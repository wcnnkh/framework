package shuchaowen.core.db.storage;

import java.util.Arrays;
import java.util.Collection;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public abstract class AbstractHotSpotDataCacheStorage extends AbstractStorage{
	private String prefix;
	private int exp;// 过期时间
	private AbstractExecuteStorage abstractExecuteStorage;
	
	public AbstractHotSpotDataCacheStorage(AbstractExecuteStorage abstractExecuteStorage, String prefix, int exp){
		super(abstractExecuteStorage.getDb(), abstractExecuteStorage.getSqlFormat());
		this.prefix = prefix;
		this.exp = exp;
		this.abstractExecuteStorage = abstractExecuteStorage;
	}

	public AbstractExecuteStorage getAbstractExecuteStorage() {
		return abstractExecuteStorage;
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
	
	public void updateToCache(Collection<Object> beans){
		for (Object bean : beans) {
			try {
				set(prefix + CacheUtils.getObjectKey(bean), exp, bean);
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
	public <T> T getById(Class<T> type, Object... params) {
		Object t = getByIdToCache(type, params);
		if(t != null){
			return (T) t;
		}
		
		t = super.getById(type, params);
		if(t != null){
			saveToCache(Arrays.asList(t));
		}
		return (T) t;
	}
	
	@Override
	public void save(Collection<Object> beans) {
		saveToCache(beans);
		super.save(beans);
	}
	
	@Override
	public void delete(Collection<Object> beans) {
		deleteToCache(beans);
		super.delete(beans);
	}
	
	@Override
	public void update(Collection<Object> beans) {
		updateToCache(beans);
		super.update(beans);
	}
	
	@Override
	public void saveOrUpdate(Collection<Object> beans) {
		updateToCache(beans);
		super.saveOrUpdate(beans);
	}
	
	public void execute(ExecuteInfo executeInfo) {
		if(abstractExecuteStorage == null){
			getDb().execute(getSqlList(executeInfo));
		}else{
			abstractExecuteStorage.execute(executeInfo);
		}
	}
}
