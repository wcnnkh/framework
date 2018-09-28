package shuchaowen.core.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.db.cache.CacheFactory;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.sql.format.Select;
import shuchaowen.core.db.sql.format.mysql.MysqlFormat;
import shuchaowen.core.db.sql.format.mysql.MysqlSelect;
import shuchaowen.core.db.storage.DefaultStorage;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.db.storage.StorageFactory;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;

public abstract class DB implements ConnectionOrigin {
	private static final Storage DEFAULT_STORAGE = new DefaultStorage();
	private static Map<String, TableInfo> tableMap = new HashMap<String, TableInfo>();
	private static Map<Class<? extends CacheFactory>, CacheFactory> cacheFactoryMap = new HashMap<Class<? extends CacheFactory>, CacheFactory>();
	
	public static final TableInfo getTableInfo(Class<?> clz) {
		return getTableInfo(clz.getName());
	}

	private static final TableInfo getTableInfo(String className) {
		String name = ClassUtils.getCGLIBRealClassName(className);
		TableInfo tableInfo = tableMap.get(name);
		if (tableInfo == null) {
			synchronized (tableMap) {
				tableInfo = tableMap.get(name);
				if (tableInfo == null) {
					tableInfo = new TableInfo(ClassUtils.getClassInfo(name));
					tableMap.put(name, tableInfo);
				}
			}
		}
		return tableInfo;
	}
	
	public static CacheFactory getCacheFactory(Class<? extends CacheFactory> cacheFactoryClass){
		if(cacheFactoryClass == null){
			return null;
		}
		
		if(CacheFactory.class.getName().equals(cacheFactoryClass.getName())){
			return null;
		}
		
		if(cacheFactoryMap.containsKey(cacheFactoryClass)){
			return cacheFactoryMap.get(cacheFactoryClass);
		}else{
			synchronized (cacheFactoryMap) {
				if(cacheFactoryMap.containsKey(cacheFactoryClass)){
					return cacheFactoryMap.get(cacheFactoryClass);
				}else{
					try {
						CacheFactory cacheFactory = cacheFactoryClass.newInstance();
						cacheFactoryMap.put(cacheFactoryClass, cacheFactory);
						return cacheFactory;
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	private volatile static DB[] dbs = new DB[0];
	
	private SQLFormat sqlFormat = new MysqlFormat();
	@Deprecated
	private boolean debug;
	private StorageFactory storageFactory;

	{
		Logger.info("Init DB for className:" + this.getClass().getName());
		synchronized (dbs) {
			DB[] arr = new DB[dbs.length + 1];
			int i = 0;
			for (; i < dbs.length; i++) {
				arr[i] = dbs[i];
			}
			arr[i] = this;
			dbs = arr;
		}
	}
	
	protected void setSqlFormat(SQLFormat sqlFormat) {
		if (sqlFormat == null) {
			throw new NullPointerException("sqlformat not is null");
		}
		this.sqlFormat = sqlFormat;
	}
	
	public final static DB[] allDB() {
		return dbs;
	}
	
	private Storage getStorage(Class<?> tableClass){
		TableInfo tableInfo = getTableInfo(tableClass);
		Storage storage = tableInfo.getStorage();
		if(storage == null && storageFactory != null){
			storage = storageFactory.getStorage(tableClass);
		}
		return storage == null? DEFAULT_STORAGE:storage;
	}

	public Select createSelect(){
		return new MysqlSelect(this);
	}

	public void execute(Collection<SQL> sqls) {
		TransactionContext.getInstance().execute(this, sqls);
	}
	
	public ResultSet select(SQL sql) {
		return TransactionContext.getInstance().select(this, sql);
	}
	
	public void execute(SQL... sqls) {
		execute(Arrays.asList(sqls));
	}
	
	public <T> List<T> select(Class<T> type, SQL sql){
		return select(sql).getList(type);
	}
	
	@Deprecated
	public <T> T selectOne(Class<T> type, SQL sql){
		return select(sql).getFirst(type);
	}
	
	public void iterator(SQL sql, TableMapping tableMapping, ResultIterator iterator){
		DBUtils.iterator(this, sql, tableMapping, iterator);
	}
	
	public void iterator(SQL sql, ResultIterator iterator){
		DBUtils.iterator(this, sql, iterator);
	}

	public void iterator(Class<?> tableClass, ResultIterator iterator){
		Select select = createSelect();
		select.from(tableClass);
		select.iterator(iterator);
	}
	
	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName,
			String columnName) {
		TableInfo tableInfo = DB.getTableInfo(tableClass);
		String tName = (tableName == null || tableName.length() == 0)? tableInfo.getName():tableName;
		SQL sql = sqlFormat.toMaxValueSQL(tableInfo, tName,
				columnName);
		ResultSet resultSet = select(sql);
		resultSet.registerClassTable(type, tName);
		return resultSet.getFirst(type);
	}
	
	public <T> T getMaxValue(Class<T> type, Class<?> tableClass,
			String columnName) {
		return getMaxValue(type, tableClass, null, columnName);
	}

	public int getMaxIntValue(Class<?> tableClass, String fieldName) {
		Integer maxId = getMaxValue(Integer.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0;
		}
		return maxId;
	}

	public long getMaxLongValue(Class<?> tableClass, String fieldName) {
		Long maxId = getMaxValue(Long.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0L;
		}
		return maxId;
	}

	public void createTable(Class<?> tableClass) {
		TableInfo tableInfo = DB.getTableInfo(tableClass);
		createTable(tableClass, tableInfo.getName());
	}

	public void createTable(Class<?> tableClass, String tableName) {
		TableInfo tableInfo = DB.getTableInfo(tableClass);
		SQL sql = sqlFormat.toCreateTableSql(tableInfo, tableName);
		Logger.info(sql.getSql());
		DBUtils.execute(this, sql);
	}

	public void createTable(String packageName) {
		Collection<Class<?>> list = ClassUtils.getClasses(packageName);
		for (Class<?> tableClass : list) {
			Table table = tableClass.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (table.create()) {
				createTable(tableClass);
			}
		}
	}

	public SQLFormat getSqlFormat() {
		return sqlFormat;
	}
	
	//storage
	public <T> T getById(Class<T> type, Object... params) {
		return getStorage(type).getById(this, getSqlFormat(), type, params);
	}
	
	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return getStorage(type).getByIdList(this, getSqlFormat(), type, params);
	}
	
	private Map<Storage, List<Object>> getBeanStorageMap(Collection<Object> beans){
		Map<Storage, List<Object>> map = new HashMap<Storage, List<Object>>();
		for(Object bean : beans){
			Storage storage = getStorage(bean.getClass());
			List<Object> list = map.get(storage);
			if(list == null){
				list = new ArrayList<Object>();
				list.add(bean);
				map.put(storage, list);
			}else{
				list.add(bean);
			}
		}
		return map;
	}

	/** 保存  **/
	public void save(Object... beans) {
		save(Arrays.asList(beans));
	}
	
	public void save(Collection<Object> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		Map<Storage, List<Object>> map = getBeanStorageMap(beans);
		for(Entry<Storage, List<Object>> entry : map.entrySet()){
			entry.getKey().save(entry.getValue(), this, getSqlFormat());
		}
	}

	/**删除**/
	public void delete(Object... beans) {
		delete(Arrays.asList(beans));
	}
	
	public void delete(Collection<Object> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		Map<Storage, List<Object>> map = getBeanStorageMap(beans);
		for(Entry<Storage, List<Object>> entry : map.entrySet()){
			entry.getKey().delete(entry.getValue(), this, getSqlFormat());
		}
	}
	
	/**更新**/
	public void update(Object... beans) {
		update(Arrays.asList(beans));
	}
	
	public void update(Collection<Object> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		Map<Storage, List<Object>> map = getBeanStorageMap(beans);
		for(Entry<Storage, List<Object>> entry : map.entrySet()){
			entry.getKey().update(entry.getValue(), this, getSqlFormat());
		}
	}
	
	/**保存或更新**/
	public void saveOrUpdate(Object ...beans){
		saveOrUpdate(Arrays.asList(beans));
	}
	
	public void saveOrUpdate(Collection<Object> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		Map<Storage, List<Object>> map = getBeanStorageMap(beans);
		for(Entry<Storage, List<Object>> entry : map.entrySet()){
			entry.getKey().saveOrUpdate(entry.getValue(), this, getSqlFormat());
		}
	}
	
	/**自增**/
	public void incr(Object obj, String field){
		incr(obj, field, 1, null);
	}

	public void incr(Object obj, String field, double limit){
		incr(obj, field, limit, null);
	}
	
	public void incr(Object obj, String field, double limit, Double maxValue){
		Storage storage = getStorage(obj.getClass());
		storage.incr(obj, field, limit, maxValue, this, getSqlFormat());
	}
	
	/**自减**/
	public void decr(Object obj, String field){
		decr(obj, field, 1, null);
	}
	
	public void decr(Object obj, String field, double limit){
		decr(obj, field, limit, null);
	}
	
	public void decr(Object obj, String field, double limit, Double minValue){
		Storage storage = getStorage(obj.getClass());
		storage.decr(obj, field, limit, minValue, this, getSqlFormat());
	}

	public boolean isDebug() {
		return debug;
	}

	protected void setDebug(boolean debug) {
		this.debug = debug;
	}
}