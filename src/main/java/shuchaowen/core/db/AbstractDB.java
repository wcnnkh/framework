package shuchaowen.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;

public abstract class AbstractDB implements AutoCloseable{
	private volatile static Map<String, TableInfo> tableMap = new HashMap<String, TableInfo>();
	private volatile static Map<String, AbstractDB> dbMap = new HashMap<String, AbstractDB>();
	
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
			
			dbMap.put(this.getClass().getName(), this);
		}
	}
	
	public static AbstractDB getAbstractDB(Class<?> abstractDBClass){
		return getAbtstractDB(abstractDBClass.getName());
	}
	
	public static AbstractDB getAbtstractDB(String name){
		AbstractDB abstractDB =  dbMap.get(name);
		if(abstractDB == null){
			throw new NullPointerException("not found [" + name + "]");
		}
		return abstractDB;
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
	
	public abstract Connection getConnection() throws SQLException;
}
