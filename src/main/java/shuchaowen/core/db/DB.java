package shuchaowen.core.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.sql.format.Select;
import shuchaowen.core.db.sql.format.mysql.MysqlFormat;
import shuchaowen.core.db.sql.format.mysql.MysqlSelect;
import shuchaowen.core.db.storage.DefaultStorage;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;

public abstract class DB extends AbstractDB {
	public static final Storage DEFAULT_STORAGE = new DefaultStorage();
	public static final SQLFormat DEFAULT_SQL_FORMAT = new MysqlFormat();
	private Map<String, Storage> storageMap = new HashMap<String, Storage>();
	
	private SQLFormat sqlFormat;
	private Storage storage;
	
	protected void registerStorage(Class<?> tableClass, Storage storage){
		synchronized (storageMap) {
			storageMap.put(ClassUtils.getCGLIBRealClassName(tableClass), storage);
		}
	}
	
	protected void setSqlFormat(SQLFormat sqlFormat) {
		if (sqlFormat == null) {
			throw new NullPointerException("sqlformat not is null");
		}
		this.sqlFormat = sqlFormat;
	}
	
	public Storage getStorage() {
		return storage == null? DEFAULT_STORAGE:storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	
	public Storage getStorage(Class<?> tableClass){
		Storage storage = storageMap.get(ClassUtils.getCGLIBRealClassName(tableClass));
		return storage == null? getStorage():storage;
	}
	
	private Map<Storage, List<Object>> getStorageBeanMap(Collection<Object> beanList){
		Map<Storage, List<Object>> map = new HashMap<Storage, List<Object>>();
		for(Object bean : beanList){
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

	public Select createSelect(){
		return new MysqlSelect(this);
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
		SQL sql = getSqlFormat().toMaxValueSQL(tableInfo, tName,
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
		SQL sql = getSqlFormat().toCreateTableSql(tableInfo, tableName);
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
		return sqlFormat == null? DEFAULT_SQL_FORMAT:sqlFormat;
	}
	
	//storage
	public <T> T getById(Class<T> type, Object... params) {
		return getStorage(type).getById(this, getSqlFormat(), type, params);
	}
	
	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return getStorage(type).getByIdList(this, getSqlFormat(), type, params);
	}

	/** 保存  **/
	public void save(Object... beans) {
		save(Arrays.asList(beans));
	}
	
	public void save(Collection<Object> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		if(storageMap.isEmpty()){
			getStorage().save(beans, this, getSqlFormat());
		}else if(beans.size() == 1){
			for(Object bean : beans){
				getStorage(bean.getClass()).save(beans, this, getSqlFormat());
				break;
			}
		}else{
			Map<Storage, List<Object>> map = getStorageBeanMap(beans);
			for(Entry<Storage, List<Object>> entry : map.entrySet()){
				entry.getKey().save(entry.getValue(), this, getSqlFormat());
			}
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
		
		if(storageMap.isEmpty()){
			getStorage().delete(beans, this, getSqlFormat());
		}else if(beans.size() == 1){
			for(Object bean : beans){
				getStorage(bean.getClass()).delete(beans, this, getSqlFormat());
				break;
			}
		}else{
			Map<Storage, List<Object>> map = getStorageBeanMap(beans);
			for(Entry<Storage, List<Object>> entry : map.entrySet()){
				entry.getKey().delete(entry.getValue(), this, getSqlFormat());
			}
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
		
		if(storageMap.isEmpty()){
			getStorage().update(beans, this, getSqlFormat());
		}else if(beans.size() == 1){
			for(Object bean : beans){
				getStorage(bean.getClass()).update(beans, this, getSqlFormat());
				break;
			}
		}else{
			Map<Storage, List<Object>> map = getStorageBeanMap(beans);
			for(Entry<Storage, List<Object>> entry : map.entrySet()){
				entry.getKey().update(entry.getValue(), this, getSqlFormat());
			}
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
		
		if(storageMap.isEmpty()){
			getStorage().saveOrUpdate(beans, this, getSqlFormat());
		}else if(beans.size() == 1){
			for(Object bean : beans){
				getStorage(bean.getClass()).saveOrUpdate(beans, this, getSqlFormat());
				break;
			}
		}else{
			Map<Storage, List<Object>> map = getStorageBeanMap(beans);
			for(Entry<Storage, List<Object>> entry : map.entrySet()){
				entry.getKey().saveOrUpdate(entry.getValue(), this, getSqlFormat());
			}
		}
	}
}