package shuchaowen.db;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.common.Iterator;
import shuchaowen.common.Logger;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.db.annoation.Table;
import shuchaowen.db.result.Result;
import shuchaowen.db.result.ResultSet;
import shuchaowen.db.sql.SQL;
import shuchaowen.db.sql.SQLFormat;
import shuchaowen.db.sql.Select;
import shuchaowen.db.sql.mysql.MysqlFormat;
import shuchaowen.db.sql.mysql.MysqlSelect;

public abstract class AbstractDB implements ConnectionSource, AutoCloseable{
	public static final SQLFormat DEFAULT_SQL_FORMAT = new MysqlFormat();
	private volatile static Map<String, TableInfo> tableMap = new HashMap<String, TableInfo>();

	public static final TableInfo getTableInfo(Class<?> clz) {
		return getTableInfo(clz.getName());
	}

	private static final TableInfo getTableInfo(String className) {
		String name = ClassUtils.getProxyRealClassName(className);
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

	public void iterator(Class<?> tableClass, Iterator<Result> iterator){
		iterator(sqlFormat.toSelectByIdSql(DB.getTableInfo(tableClass), null), iterator);
	}
	
	public void iterator(SQL sql, final Iterator<Result> iterator){
		DBUtils.iterator(this, sql, new Iterator<java.sql.ResultSet>() {

			public void iterator(java.sql.ResultSet data) {
				try {
					iterator.iterator(new Result(data));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ResultSet select(SQL sql) {
		return TransactionContext.getInstance().select(this, sql);
	}
	
	public <T> List<T> select(Class<T> type, SQL sql){
		return select(sql).getList(type);
	}
	
	public <T> T selectOne(Class<T> type, SQL sql){
		return select(sql).getObject(type, 0);
	}
	
	public Select createSelect(){
		return new MysqlSelect(this);
	}
	
	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName,
			String columnName) {
		Select select = createSelect();
		select.desc(tableClass, columnName);
		return select.getResultSet().getObject(type, 0, tableName);
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
		return resultSet.getObject(type, 0, tName);
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
		return resultSet.getList(type, tName);
	}
	
	public void execute(Collection<OperationBean> operationBeans){
		Collection<SQL> sqls = DBUtils.getSqlList(getSqlFormat(), operationBeans);
		if(sqls == null || sqls.isEmpty()){
			return;
		}
		
		TransactionContext.getInstance().execute(this, sqls);
	}
}
