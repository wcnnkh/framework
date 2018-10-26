package shuchaowen.core.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.db.result.Result;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.sql.format.Select;
import shuchaowen.core.db.sql.format.mysql.MysqlFormat;
import shuchaowen.core.db.sql.format.mysql.MysqlSelect;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;

public abstract class AbstractDB implements ConnectionPool{
	public static final SQLFormat DEFAULT_SQL_FORMAT = new MysqlFormat();
	private volatile static Map<String, TableInfo> tableMap = new HashMap<String, TableInfo>();

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
	}
	
	private SQLFormat sqlFormat;
	
	public AbstractDB(SQLFormat sqlFormat){
		this.sqlFormat = sqlFormat;
	}
	
	public SQLFormat getSqlFormat() {
		return sqlFormat == null? DEFAULT_SQL_FORMAT:sqlFormat;
	}
	
	public void setSqlFormat(SQLFormat sqlFormat) {
		this.sqlFormat = sqlFormat;
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
		DBUtils.iterator(this, sql, null, iterator);
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

	public void createTable(Class<?> tableClass, String tableName) {
		TableInfo tableInfo = DB.getTableInfo(tableClass);
		SQL sql = getSqlFormat().toCreateTableSql(tableInfo, tableName);
		Logger.info(sql.getSql());
		DBUtils.execute(this, Arrays.asList(sql));
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
	
	public List<SQL> getSaveSqlList(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
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
	
	public List<SQL> getUpdateSqlList(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
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
	
	public List<SQL> getDeleteSqlList(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
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
	
	public List<SQL> getSaveOrUpdateSqlList(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
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
	
	public <T> T getByIdFromDB(Class<T> type, String tableName, Object... params) {
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
			throw new NullPointerException("params length not equals primary key lenght");
		}

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		SQL sql = getSqlFormat().toSelectByIdSql(tableInfo, tName, params);
		ResultSet resultSet = select(sql);
		resultSet.registerClassTable(type, tName);
		return resultSet.getFirst(type);
	}
	
	public <T> List<T> getByIdListFromDB(Class<T> type, String tableName,
			Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DB.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new NullPointerException("params length  greater than primary key lenght");
		}

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		ResultSet resultSet = select(getSqlFormat().toSelectByIdSql(tableInfo, tName, params));
		resultSet.registerClassTable(type, tName);
		return resultSet.getList(type);
	}
	
	public <T> PrimaryKeyValue<T> getByIdFromDB(Class<T> type,
			String tableName, Collection<PrimaryKeyParameter> primaryKeyParameters) {
		if(primaryKeyParameters == null || primaryKeyParameters.isEmpty()){
			return new PrimaryKeyValue<T>();
		}
		
		TableInfo tableInfo = DB.getTableInfo(type);
		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		SQL sql = getSqlFormat().toSelectINId(tableInfo, tName, primaryKeyParameters);
		ResultSet resultSet = select(sql);
		resultSet.registerClassTable(type, tName);
		List<T> list = resultSet.getList(type);
		
		PrimaryKeyValue<T> primaryKeyValue = new PrimaryKeyValue<T>();
		for (T t : list) {
			try {
				primaryKeyValue.put(tableInfo.getPrimaryKeyParameter(t), t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return primaryKeyValue;
	}
	
	public void saveToDB(Collection<?> beans){
		execute(getSaveSqlList(beans));
	}
	
	public void updateToDB(Collection<?> beans){
		execute(getUpdateSqlList(beans));
	}
	
	public void deleteToDB(Collection<?> beans){
		execute(getDeleteSqlList(beans));
	}
	
	public void saveOrUpdateToDB(Collection<?> beans){
		execute(getSaveOrUpdateSqlList(beans));
	}
	
	public void forceSave(Collection<?> beans){
		DBUtils.execute(this, getSaveSqlList(beans));
	}
	
	public void forceUpdate(Collection<?> beans){
		DBUtils.execute(this, getUpdateSqlList(beans));
	}
	
	public void forceDelete(Collection<?> beans){
		DBUtils.execute(this, getDeleteSqlList(beans));
	}
	
	public void forceSaveOrUpdate(Collection<?> beans){
		DBUtils.execute(this, getSaveOrUpdateSqlList(beans));
	}
}
