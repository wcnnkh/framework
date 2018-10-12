package shuchaowen.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.db.result.Result;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.Select;
import shuchaowen.core.db.sql.format.mysql.CreateTableSQL;
import shuchaowen.core.db.sql.format.mysql.MysqlSelect;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;

public abstract class AbstractDB implements AutoCloseable{
	private volatile static Map<String, TableInfo> tableMap = new HashMap<String, TableInfo>();
	private volatile static Map<Class<?>, AbstractDB> dbMap = new HashMap<Class<?>, AbstractDB>();
	
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
	
	{
		Logger.info("Init DB for className:" + this.getClass().getName());
		if(dbMap.containsKey(this.getClass().getName())){
			throw new ShuChaoWenRuntimeException("db is singleton for class[" + this.getClass().getName() + "]");
		}
		
		synchronized (dbMap) {
			if(dbMap.containsKey(this.getClass().getName())){
				throw new ShuChaoWenRuntimeException("db is singleton for class[" + this.getClass().getName() + "]");
			}
			
			dbMap.put(this.getClass(), this);
		}
	}
	
	public static AbstractDB getAbstractDB(Class<? extends AbstractDB> abstractDBClass){
		AbstractDB abstractDB =  dbMap.get(abstractDBClass);
		if(abstractDB == null){
			throw new NullPointerException("not found [" + abstractDBClass + "]");
		}
		return abstractDB;
	}
	
	public void iterator(Class<?> tableClass, ResultIterator iterator){
		Select select = createSelect();
		select.from(tableClass);
		select.iterator(iterator);
	}
	
	public void iterator(SQL sql, TableMapping tableMapping, ResultIterator iterator){
		DBUtils.iterator(this, sql, tableMapping, iterator);
	}
	
	public void iterator(SQL sql, ResultIterator iterator){
		DBUtils.iterator(this, sql, iterator);
	}
	
	public ResultSet select(SQL sql) {
		return TransactionContext.getInstance().select(this, sql);
	}
	
	public <T> List<T> select(Class<T> type, SQL sql){
		return select(sql).getList(type);
	}
	
	@Deprecated
	public <T> T selectOne(Class<T> type, SQL sql){
		return select(sql).getFirst(type);
	}
	
	public void execute(Collection<SQL> sqls){
		TransactionContext.getInstance().execute(this, sqls);
	}
	
	public void execute(SQL ...sqls){
		TransactionContext.getInstance().execute(this, sqls);
	}
	
	public Select createSelect(){
		return new MysqlSelect(this);
	}
	
	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName,
			String columnName) {
		Select select = createSelect();
		select.registerTableName(tableClass, tableName);
		select.desc(tableClass, columnName);
		Result result = select.getFirstResult();
		return result.getValue(tableClass, columnName);
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
	
	protected CreateTableSQL getCreateTable(TableInfo tableInfo, String tableName){
		return new CreateTableSQL(tableInfo, tableName);
	}

	public void createTable(Class<?> tableClass, String tableName) {
		TableInfo tableInfo = DB.getTableInfo(tableClass);
		SQL sql = getCreateTable(tableInfo, tableName);
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
	
	public abstract Connection getConnection() throws SQLException;
}
