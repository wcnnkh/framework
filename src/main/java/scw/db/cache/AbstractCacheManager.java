package scw.db.cache;

import scw.core.utils.ArrayUtils;
import scw.data.Cache;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public abstract class AbstractCacheManager implements CacheManager {
	public abstract Cache getCache();

	public abstract String formatKey(String key);

	public void save(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length == 0) {
			return;
		}

		getCache().add(ORMUtils.getObjectKey(tableInfo, bean), bean);
	}

	public void update(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length == 0) {
			return;
		}

		getCache().set(formatKey(ORMUtils.getObjectKey(tableInfo, bean)), bean);
	}

	public void delete(Object bean) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length == 0) {
			return;
		}

		getCache().delete(formatKey(ORMUtils.getObjectKey(tableInfo, bean)));
	}

	public void deleteById(Class<?> type, Object... params) {
		if (ArrayUtils.isEmpty(params)) {
			return;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length != params.length) {
			return;
		}

		getCache().delete(formatKey(ORMUtils.getObjectKeyById(tableInfo.getSource(), params)));
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

		return getCache().get(formatKey(ORMUtils.getObjectKeyById(type, params)));
	}

	public boolean isExistById(Class<?> type, Object... params) {
		if (ArrayUtils.isEmpty(params)) {
			return false;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo == null || tableInfo.getPrimaryKeyColumns().length != params.length) {
			return false;
		}

		return getCache().isExist(formatKey(ORMUtils.getObjectKeyById(type, params)));
	}
}
