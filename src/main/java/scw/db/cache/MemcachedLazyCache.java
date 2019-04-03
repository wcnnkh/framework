package scw.db.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.common.exception.ParameterException;
import scw.memcached.Memcached;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public class MemcachedLazyCache implements Cache {
	private static final String PREFIX = "lazy:";

	private final Memcached memcached;
	private final int exp;

	public MemcachedLazyCache(Memcached memcached, int exp) {
		this.memcached = memcached;
		this.exp = exp;
	}

	private String getObjectKey(TableInfo tableInfo, Object bean)
			throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
		for (ColumnInfo c : tableInfo.getPrimaryKeyColumns()) {
			sb.append("&");
			sb.append(c.getFieldInfo().forceGet(bean));
		}
		return sb.toString();
	}

	private String getObjectKeyById(TableInfo tableInfo, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < params.length; i++) {
			sb.append("&");
			sb.append(params[i]);
		}
		return sb.toString();
	}

	public void save(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		memcached.add(getObjectKey(tableInfo, bean), exp, bean);
	}

	public void update(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		memcached.set(getObjectKey(tableInfo, bean), exp, bean);
	}

	public void delete(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		memcached.delete(getObjectKey(tableInfo, bean));
	}

	public void saveOrUpdate(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		memcached.set(getObjectKey(tableInfo, bean), exp, bean);
	}

	public <T> T getById(Class<T> type, Object... params) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		return memcached.getAndTocuh(getObjectKeyById(tableInfo, params), exp);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) throws Throwable {
		//不支持
		return null;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) throws Throwable {
		//不支持
		return null;
	}

}
