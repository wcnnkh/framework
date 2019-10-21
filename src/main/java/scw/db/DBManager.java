package scw.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.exception.AlreadyExistsException;
import scw.core.utils.ClassUtils;
import scw.sql.Sql;

/**
 * 只用于处理默认的数据库 不再推荐使用
 * 
 * @author shuchaowen
 *
 */
public final class DBManager {
	private DBManager() {
	};

	private static final Map<Class<?>, DB> CLASS_TO_DB = new IdentityHashMap<Class<?>, DB>();

	/**
	 * 向指定数据库中注册表
	 * 
	 * @param clazz
	 * @param db
	 */
	public synchronized static void register(Class<?> clazz, DB db) {
		if (CLASS_TO_DB.containsKey(clazz)) {
			DB originDB = CLASS_TO_DB.get(clazz);
			throw new AlreadyExistsException(clazz + "已经存在了:" + originDB.getClass().getName());
		}

		CLASS_TO_DB.put(clazz, db);
	}

	/**
	 * 获取此类对应的数据库
	 * 
	 * @param tableClass
	 * @return
	 */
	public static DB getDB(Class<?> tableClass) {
		DB db = CLASS_TO_DB.get(ClassUtils.getUserClass(tableClass));
		if (db == null) {
			throw new NullPointerException(tableClass + " not found db");
		}
		return db;
	}

	public static <T> List<T> select(Class<T> type, Sql sql) {
		return getDB(type).select(type, sql);
	}

	public static <T> T selectOne(Class<T> type, Sql sql) {
		return getDB(type).selectOne(type, sql);
	}

	/**
	 * 根据主键获取数据
	 * 
	 * @param clz
	 * @param params
	 * @return
	 */
	public static <T> T getById(Class<T> clz, Object... params) {
		return getDB(clz).getById(clz, params);
	}

	/**
	 * 根据主键获取数据列表
	 * 
	 * @param clz
	 * @param params
	 * @return
	 */
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

	/**
	 * 保存数据
	 * 
	 * @param bean
	 */
	public static void save(Object... beans) {
		for (Entry<DB, List<Object>> entry : getMap(beans).entrySet()) {
			List<Object> list = entry.getValue();
			for (Object bean : list) {
				entry.getKey().save(bean);
			}
		}
	}

	/**
	 * 删除数据
	 * 
	 * @param bean
	 */
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