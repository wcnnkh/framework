package shuchaowen.core.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.db.cache.Cache;
import shuchaowen.core.db.cache.CacheFactory;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.sql.format.Select;
import shuchaowen.core.db.sql.format.mysql.MysqlFormat;
import shuchaowen.core.db.sql.format.mysql.MysqlSelect;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;

public abstract class DB implements ConnectionOrigin {
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
	private CacheFactory cacheFactory;

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

	public CacheFactory getCacheFactory() {
		return cacheFactory;
	}

	protected void setCacheFactory(CacheFactory cacheFactory) {
		this.cacheFactory = cacheFactory;
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
	
	private Cache getCache(Class<?> tableClass, TableInfo tableInfo){
		Cache cache = tableInfo.getCache();
		if(cache == null && cacheFactory != null){
			cache = cacheFactory.getCache(tableClass);
		}
		return cache;
	}
	
	public void iterator(SQL sql, TableMapping tableMapping, ResultIterator iterator){
		DBUtils.iterator(this, sql, tableMapping, iterator);
	}
	
	public void iterator(SQL sql, ResultIterator iterator){
		DBUtils.iterator(this, sql, iterator);
	}
	
	private Cache getCache(Object bean){
		TableInfo tableInfo = getTableInfo(bean.getClass());
		return tableInfo == null? null:getCache(bean.getClass(), tableInfo);
	}
	
	public <T> T getByIdWithTableName(Class<T> type, String tableName, Object ...params){
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DB.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (tableInfo.getPrimaryKeyColumns().length != params.length) {
			throw new NullPointerException(
					"params length not equals primary key lenght");
		}
		
		String tName = (tableName == null || tableName.length() == 0)? tableInfo.getName():tableName;
		T t = null;
		Cache cache = getCache(type, tableInfo);
		if(cache != null){
			t = cache.getById(type, tName, params);
		}
		
		if(t == null){
			SQL sql = getSqlFormat().toSelectByIdSql(tableInfo, tName, params);
			ResultSet resultSet = select(sql);
			resultSet.registerClassTable(type, tName);
			t = resultSet.getFirst(type);
		}
		
		if(t != null && cache != null){
			cache.save(t);
		}
		return t;
	}
	
	public <T> T getById(Class<T> type, Object... params) {
		return getByIdWithTableName(type, null, params);
	}

	public <T> List<T> getByIdList(Class<T> type, String tableName, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DB.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new NullPointerException(
					"params length  greater than primary key lenght");
		}
		
		String tName = (tableName == null || tableName.length() == 0)? tableInfo.getName():tableName;
		ResultSet resultSet = select(getSqlFormat().toSelectByIdSql(tableInfo, tName, params));
		resultSet.registerClassTable(type, tName);
		return resultSet.getList(type);
	}
	
	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return getByIdList(type, null, params);
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
	
	private List<SQL> getDeleteSqlList(Collection<Object> beans){
		if (beans == null) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(getSqlFormat().toDeleteSql(obj));
		}
		return sqls;
	}
	
	private List<SQL> getSaveSqlList(Collection<Object> beans){
		if (beans == null) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			
			sqls.add(getSqlFormat().toInsertSql(obj));
		}
		return sqls;
	}
	
	private List<SQL> getUpdateSqlList(Collection<Object> beans){
		if (beans == null) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(getSqlFormat().toUpdateSql(obj));
		}
		return sqls;
	}
	
	private List<SQL> getSaveOrUpdateSqlList(Collection<Object> beans){
		if (beans == null) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(getSqlFormat().toSaveOrUpdateSql(obj));
		}
		return sqls;
	}

	/** 保存  **/
	public void save(Object... beans) {
		save(Arrays.asList(beans));
	}
	
	public void save(Collection<Object> beans){
		saveToCache(beans);
		execute(getSaveSqlList(beans));
	}
	
	private void saveToCache(Collection<Object> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		for(Object obj : beans){
			Cache cache = getCache(obj);
			if(cache == null){
				continue;
			}
			cache.save(obj);
		}
	}

	/**删除**/
	public void delete(Object... beans) {
		delete(Arrays.asList(beans));
	}
	
	public void delete(Collection<Object> beans){
		deleteToCache(beans);
		execute(getDeleteSqlList(beans));
	}
	
	private void deleteToCache(Collection<Object> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		for(Object obj : beans){
			Cache cache = getCache(obj);
			if(cache == null){
				continue;
			}
			
			cache.delete(obj);
		}
	}
	
	/**更新**/
	public void update(Object... beans) {
		update(Arrays.asList(beans));
	}
	
	public void update(Collection<Object> beans){
		updateToCache(beans);
		execute(getUpdateSqlList(beans));
	}
	
	private void updateToCache(Collection<Object> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		for(Object obj : beans){
			Cache cache = getCache(obj);
			if(cache == null){
				continue;
			}
			
			cache.update(obj);
		}
	}
	
	/**保存或更新**/
	public void saveOrUpdate(Object ...beans){
		saveOrUpdate(Arrays.asList(beans));
	}
	
	public void saveOrUpdate(Collection<Object> beans){
		saveOrUpdateToCache(beans);
		execute(getSaveOrUpdateSqlList(beans));
	}
	
	private void saveOrUpdateToCache(Collection<Object> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		for(Object obj : beans){
			Cache cache = getCache(obj);
			if(cache == null){
				continue;
			}
			
			cache.saveOrUpdate(obj);
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
		SQL sql = sqlFormat.toIncrSql(obj, field, limit, maxValue);
		execute(sql);
	}
	
	/**自减**/
	public void decr(Object obj, String field){
		decr(obj, field, 1, null);
	}
	
	public void decr(Object obj, String field, double limit){
		decr(obj, field, limit, null);
	}
	
	public void decr(Object obj, String field, double limit, Double minValue){
		SQL sql = sqlFormat.toDecrSql(obj, field, limit, minValue);
		execute(sql);
	}

	public boolean isDebug() {
		return debug;
	}

	protected void setDebug(boolean debug) {
		this.debug = debug;
	}
}