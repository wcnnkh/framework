package scw.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.ClassUtils;
import scw.database.SQL;
import scw.database.annoation.Table;

/**
 * 只用于处理默认的数据库
 * 
 * @author shuchaowen
 *
 */
public final class DBManager {
	private DBManager(){};
	private static final Map<String, DB> CLASS_NAME_TO_DB = new HashMap<String, DB>();

	private static void createTable(DB db, Class<?> tableClass){
		Table table = tableClass.getAnnotation(Table.class);
		if(table == null || table.create()){
			db.createTable(tableClass);
		}
	}
	
	/**
	 * 向指定数据库中注册表
	 * @param db 数据库对象
	 * @param packageName 扫描的路径
	 * @param create 是否自动创建表
	 */
	public static void register(DB db, String packageName, boolean create) {
		Collection<Class<?>> list = ClassUtils.getClasses(packageName);
		for (Class<?> tableClass : list) {
			String name = ClassUtils.getProxyRealClassName(tableClass.getName());
			if (CLASS_NAME_TO_DB.containsKey(name)) {
				continue;
			}
			
			Table table = tableClass.getAnnotation(Table.class);
			if(table == null){
				continue;
			}

			synchronized (CLASS_NAME_TO_DB) {
				if (CLASS_NAME_TO_DB.containsKey(name)) {
					continue;
				}

				CLASS_NAME_TO_DB.put(name, db);
				if (create) {
					createTable(db, tableClass);
				}
			}
		}
	}

	/**
	 * 向指定数据库中注册一个表
	 * @param db 数据库对象
	 * @param tableClass 表所对应的类
	 * @param create 是否自动创建表
	 */
	public static void register(DB db, Class<?> tableClass, boolean create) {
		register(db, tableClass, false, create);
	}

	/**
	 * 向指定数据库中注册一个表
	 * @param db 数据库对象
	 * @param tableClass 表所对应的类
	 * @param force 是否强制注册
	 * @param create 是否自动创建表
	 */
	public static void register(DB db, Class<?> tableClass, boolean force, boolean create) {
		String name = ClassUtils.getProxyRealClassName(tableClass.getName());
		if (force) {
			synchronized (CLASS_NAME_TO_DB) {
				CLASS_NAME_TO_DB.put(name, db);
				if (create) {
					createTable(db, tableClass);
				}
			}
		} else {
			if (CLASS_NAME_TO_DB.containsKey(name)) {
				throw new ShuChaoWenRuntimeException(name + "已经存在了");
			} else {
				synchronized (CLASS_NAME_TO_DB) {
					if (CLASS_NAME_TO_DB.containsKey(name)) {
						throw new ShuChaoWenRuntimeException(name + "已经存在了");
					} else {
						CLASS_NAME_TO_DB.put(name, db);
						if (create) {
							createTable(db, tableClass);
						}
					}
				}
			}
		}
	}

	/**
	 * 获取此类对应的数据库
	 * @param tableClass
	 * @return
	 */
	public static DB getDB(Class<?> tableClass) {
		String name = ClassUtils.getProxyRealClassName(tableClass);
		DB db = CLASS_NAME_TO_DB.get(name);
		if(db == null){
			throw new NullPointerException(name + " not found db");
		}
		return db;
	}
	
	public static <T> List<T> select(Class<T> type, SQL sql){
		return getDB(type).select(type, sql);
	}
	
	public static <T> T selectOne(Class<T> type, SQL sql){
		return getDB(type).selectOne(type, sql);
	}
	
	/**
	 * 根据主键获取数据
	 * @param clz
	 * @param params
	 * @return
	 */
	public static <T> T getById(Class<T> clz, Object... params) {
		return getDB(clz).getById(clz, params);
	}

	/**
	 * 根据主键获取数据列表
	 * @param clz
	 * @param params
	 * @return
	 */
	public static <T> List<T> getByIdList(Class<T> clz, Object... params) {
		return getDB(clz).getByIdList(clz, params);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<DB, List<Object>> getMap(Object ...beans){
		Map<DB, List<Object>> map = new HashMap<DB, List<Object>>();
		for(Object obj : beans){
			DB db = getDB(obj.getClass());
			List<Object> list = map.getOrDefault(db, new ArrayList<Object>());
			if(obj instanceof Collection){
				list.addAll((Collection)obj);
			}else if(obj.getClass().isArray()){
				list.addAll(Arrays.asList((Object[])obj));
			}else{
				list.add(obj);
			}
			map.put(db, list);
		}
		return map;
	}

	/**
	 * 保存数据
	 * @param bean
	 */
	public static void save(Object ...beans) {
		for(Entry<DB, List<Object>> entry : getMap(beans).entrySet()){
			entry.getKey().save(entry.getValue());
		}
	}

	/**
	 * 删除数据
	 * @param bean
	 */
	public static void delete(Object ...beans) {
		for(Entry<DB, List<Object>> entry : getMap(beans).entrySet()){
			entry.getKey().delete(entry.getValue());
		}
	}
	
	public static void update(Object ...beans){
		for(Entry<DB, List<Object>> entry : getMap(beans).entrySet()){
			entry.getKey().update(entry.getValue());
		}
	}
	
	public static void saveOrUpdate(Object ...beans){
		for(Entry<DB, List<Object>> entry : getMap(beans).entrySet()){
			entry.getKey().saveOrUpdate(entry.getValue());
		}
	}
}