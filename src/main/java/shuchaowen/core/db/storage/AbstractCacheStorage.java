package shuchaowen.core.db.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;

public abstract class AbstractCacheStorage extends CommonStorage {
	public AbstractCacheStorage(AbstractDB db, Storage execute) {
		super(db, null, execute);
	}

	public abstract <T> T getByIdFromCache(Class<T> type, Object... params);

	public abstract <T> Map<PrimaryKeyParameter, T> getByIdFromCache(
			Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters);

	public abstract void saveToCache(Collection<?> beans);

	public abstract void updateToCache(Collection<?> beans);

	public abstract void saveOrUpdateToCache(Collection<?> beans);

	public abstract void deleteToCache(Collection<?> beans);

	public abstract boolean getByIdExist(Class<?> type, Object... params);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getById(Class<T> type, Object... params) {
		Object t = getByIdFromCache(type, params);
		if (t != null) {
			return (T) t;
		}

		// 用于防止大量无效的请求而导致的数据库压力过大，要处理因缓存穿透导致的问题应该使用
		if (getByIdExist(type, params)) {
			return null;
		}

		t = super.getById(type, params);
		if (t != null) {
			saveToCache(Arrays.asList(t));
		}
		return (T) t;
	}

	@Override
	public <T> Map<PrimaryKeyParameter, T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		Map<PrimaryKeyParameter, T> map = getByIdFromCache(type,
				primaryKeyParameters);
		if (map == null || map.size() != primaryKeyParameters.size()) {
			List<PrimaryKeyParameter> notFindList = null;
			for (PrimaryKeyParameter parameter : primaryKeyParameters) {
				if (map != null && map.containsKey(parameter)) {
					continue;
				}

				if (getByIdExist(type, parameter.getParams())) {
					continue;
				}

				if (notFindList == null) {
					notFindList = new ArrayList<PrimaryKeyParameter>();
				}
				notFindList.add(parameter);
			}

			if (notFindList != null && !notFindList.isEmpty()) {
				Map<PrimaryKeyParameter, T> dbDataMap = super.getById(type,
						notFindList);
				if (dbDataMap != null && !dbDataMap.isEmpty()) {
					if(map == null){
						map = dbDataMap;
					}else{
						map.putAll(dbDataMap);
					}
					saveToCache(dbDataMap.values());
				}
			}
		}
		return map;
	}

	@Override
	public void save(Collection<?> beans) {
		super.save(beans);
		saveToCache(beans);
	}

	@Override
	public void delete(Collection<?> beans) {
		super.delete(beans);
		deleteToCache(beans);
	}

	@Override
	public void update(Collection<?> beans) {
		super.update(beans);
		updateToCache(beans);
	}

	@Override
	public void saveOrUpdate(Collection<?> beans) {
		super.saveOrUpdate(beans);
		saveOrUpdateToCache(beans);
	}
}
