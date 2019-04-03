package scw.db.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.common.Constants;
import scw.common.exception.ParameterException;
import scw.redis.Redis;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

public class RedisLazyCache implements Cache {
	private static final String PREFIX = "lazy:";

	private final Redis redis;
	private final int exp;

	public RedisLazyCache(Redis redis, int exp) {
		this.redis = redis;
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

		redis.set(getObjectKey(tableInfo, bean).getBytes(Constants.DEFAULT_CHARSET), CacheUtils.encode(bean),
				Redis.NX.getBytes(Constants.DEFAULT_CHARSET), Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
	}

	public void update(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		redis.set(getObjectKey(tableInfo, bean).getBytes(Constants.DEFAULT_CHARSET), CacheUtils.encode(bean),
				Redis.XX.getBytes(Constants.DEFAULT_CHARSET), Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
	}

	public void delete(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		redis.delete(getObjectKey(tableInfo, bean).getBytes(Constants.DEFAULT_CHARSET));
	}

	public void saveOrUpdate(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		redis.set(getObjectKey(tableInfo, bean).getBytes(Constants.DEFAULT_CHARSET), CacheUtils.encode(bean),
				Redis.XX.getBytes(Constants.DEFAULT_CHARSET), Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
	}

	public <T> T getById(Class<T> type, Object... params) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		byte[] data = redis.getAndTouch(getObjectKeyById(tableInfo, params).getBytes(Constants.DEFAULT_CHARSET), exp);
		if (data == null) {
			return null;
		}

		return CacheUtils.decode(type, data);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) throws Throwable {
		// 不支持
		return null;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) throws Throwable {
		// 不支持
		return null;
	}

}
