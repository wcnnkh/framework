package scw.db.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.Constants;
import scw.common.exception.ParameterException;
import scw.redis.Redis;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.TableInfo;

/**
 * 缓存所有的数据
 * 
 * @author shuchaowen
 *
 */
public final class RedisFullCache implements Cache {
	private static final String PREFIX = "full:";
	private final Redis redis;
	private final int exp;
	private final boolean allIndex;// 是否维护所有的索引

	public RedisFullCache(Redis redis, int exp, boolean allIndex) {
		this.redis = redis;
		this.exp = exp;
		this.allIndex = allIndex;
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
		Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		String objectKey = getObjectKeyById(tableInfo, args);
		redis.set(objectKey.getBytes(Constants.DEFAULT_CHARSET), CacheUtils.encode(bean),
				Redis.NX.getBytes(Constants.DEFAULT_CHARSET), Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < args.length; i++) {
			if ((allIndex || i > 0) && i < args.length - 1) {
				String indexKey = sb.toString();
				redis.hset(indexKey, args[i].toString(), objectKey);
			}
			sb.append("&");
			sb.append(args[i]);
		}
	}

	public void update(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String objectKey = getObjectKey(tableInfo, bean);
		redis.set(objectKey.getBytes(Constants.DEFAULT_CHARSET), CacheUtils.encode(bean),
				Redis.XX.getBytes(Constants.DEFAULT_CHARSET), Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
	}

	public void delete(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		String objectKey = getObjectKeyById(tableInfo, args);
		redis.delete(objectKey.getBytes(Constants.DEFAULT_CHARSET));
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < args.length; i++) {
			if ((allIndex || i > 0) && i < args.length - 1) {
				String indexKey = sb.toString();
				redis.hdel(indexKey, args[i].toString());
			}
			sb.append("&");
			sb.append(args[i]);
		}
	}

	public void saveOrUpdate(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		String objectKey = getObjectKeyById(tableInfo, args);
		redis.set(objectKey.getBytes(Constants.DEFAULT_CHARSET), CacheUtils.encode(bean),
				Redis.XX.getBytes(Constants.DEFAULT_CHARSET), Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < args.length; i++) {
			if ((allIndex || i > 0) && i < args.length - 1) {
				String indexKey = sb.toString();
				redis.hset(indexKey, args[i].toString(), objectKey);
			}
			sb.append("&");
			sb.append(args[i]);
		}
	}

	public <T> T getById(Class<T> type, Object... params) {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < params.length; i++) {
			sb.append("&");
			sb.append(params[i]);
		}

		byte[] data = redis.getAndTouch(sb.toString().getBytes(Constants.DEFAULT_CHARSET), exp);
		if (data == null) {
			return null;
		}
		return CacheUtils.decode(type, data);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		if (!allIndex && (params == null || params.length == 0)) {
			throw new ParameterException("此缓存方式至少需要一个以上的主键支持");
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		if (!allIndex && tableInfo.getPrimaryKeyColumns().length <= 1) {
			throw new ParameterException("主键数量错误，至少要两个主键");
		}

		if (tableInfo.getPrimaryKeyColumns().length == params.length) {
			T t = getById(type, params);
			if (t == null) {
				return null;
			}

			return Arrays.asList(t);
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new ParameterException("主键参数错误");
		}

		String key = getObjectKeyById(tableInfo, params);
		Map<String, String> keyMap = redis.hGetAll(key);
		if (keyMap == null) {
			return null;
		}

		Map<byte[], byte[]> valueMap = redis.get(keyMap.values().toArray(new byte[0][]));
		if (valueMap == null || valueMap.isEmpty()) {
			return null;
		}

		List<T> list = new ArrayList<T>(valueMap.size());
		for (Entry<String, String> entry : keyMap.entrySet()) {
			byte[] k = entry.getValue().getBytes(Constants.DEFAULT_CHARSET);
			byte[] value = valueMap.get(k);
			if (value == null) {
				continue;
			}

			list.add(CacheUtils.decode(type, value));
		}
		return list;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds, Object... params) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (!allIndex && tableInfo.getPrimaryKeyColumns().length <= 1) {
			throw new ParameterException("主键数量错误，至少要两个主键");
		}

		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new ParameterException("至少需要一个主键");
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new ParameterException("主键参数错误");
		}

		String indexKey = getObjectKeyById(tableInfo, params);
		Map<String, String> keyMap = redis.hGetAll(indexKey);
		if (keyMap == null) {
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();
		for (K k : inIds) {
			if (k == null) {
				continue;
			}

			String key = k.toString();
			String objectKey = keyMap.get(key);
			map.put(objectKey, key);
		}

		if (map.isEmpty()) {
			return null;
		}

		Map<byte[], byte[]> valueMap = redis.get(keyMap.values().toArray(new byte[0][]));
		Map<K, V> result = new HashMap<K, V>(valueMap.size());
		for (K k : inIds) {
			if (k == null) {
				continue;
			}

			String key = k.toString();
			String objectKey = keyMap.get(key);
			if (objectKey == null) {
				continue;
			}

			byte[] data = valueMap.get(objectKey.getBytes(Constants.DEFAULT_CHARSET));
			if (data == null) {
				continue;
			}

			result.put(k, CacheUtils.decode(type, data));
		}
		return result;
	}

}
