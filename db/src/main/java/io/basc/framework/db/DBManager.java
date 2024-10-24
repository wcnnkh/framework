package io.basc.framework.db;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.sql.Sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class DBManager {
	private DBManager() {
	};

	private static final Map<Class<?>, DB> CLASS_TO_DB = new IdentityHashMap<Class<?>, DB>();

	public static void register(Class<?> clazz, DB db) {
		synchronized (CLASS_TO_DB) {
			if (CLASS_TO_DB.containsKey(clazz)) {
				DB originDB = CLASS_TO_DB.get(clazz);
				throw new AlreadyExistsException(clazz + "已经存在了:" + originDB.getClass().getName());
			}

			CLASS_TO_DB.put(clazz, db);
		}
	}

	public static boolean unregister(Class<?> clazz) {
		synchronized (CLASS_TO_DB) {
			return CLASS_TO_DB.remove(clazz) != null;
		}
	}

	public static DB getDB(Class<?> tableClass) {
		DB db = CLASS_TO_DB.get(ProxyUtils.getFactory().getUserClass(tableClass));
		if (db == null) {
			throw new NullPointerException(tableClass + " not found db");
		}
		return db;
	}

	public static <T> List<T> select(Class<T> type, Sql sql) {
		return getDB(type).query(type, sql).toList();
	}

	public static <T> T selectOne(Class<T> type, Sql sql) {
		return getDB(type).query(type, sql).first();
	}

	public static <T> T getById(Class<T> clz, Object... params) {
		return getDB(clz).getById(clz, params);
	}

	public static <T> List<T> getByIdList(Class<T> clz, Object... params) {
		return getDB(clz).getByIdList(clz, params);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<DB, List<Object>> getMap(Object... beans) {
		Map<DB, List<Object>> map = new HashMap<DB, List<Object>>();
		for (Object obj : beans) {
			DB db = getDB(obj.getClass());
			List<Object> list = map.getOrDefault(db, new ArrayList<Object>());
			if (obj instanceof Collection) {
				list.addAll((Collection) obj);
			} else if (obj.getClass().isArray()) {
				list.addAll(Arrays.asList((Object[]) obj));
			} else {
				list.add(obj);
			}
			map.put(db, list);
		}
		return map;
	}

	public static void save(Object... beans) {
		for (Entry<DB, List<Object>> entry : getMap(beans).entrySet()) {
			List<Object> list = entry.getValue();
			for (Object bean : list) {
				entry.getKey().save(bean);
			}
		}
	}

	public static void delete(Object... beans) {
		for (Entry<DB, List<Object>> entry : getMap(beans).entrySet()) {
			List<Object> list = entry.getValue();
			for (Object bean : list) {
				entry.getKey().delete(bean);
			}
		}
	}

	public static void update(Object... beans) {
		for (Entry<DB, List<Object>> entry : getMap(beans).entrySet()) {
			List<Object> list = entry.getValue();
			for (Object bean : list) {
				entry.getKey().update(bean);
			}
		}
	}

	public static void saveOrUpdate(Object... beans) {
		for (Entry<DB, List<Object>> entry : getMap(beans).entrySet()) {
			List<Object> list = entry.getValue();
			for (Object bean : list) {
				entry.getKey().saveOrUpdate(bean);
			}
		}
	}
}