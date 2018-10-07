package shuchaowen.core.db.storage;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.sql.format.mysql.MysqlFormat;
import shuchaowen.core.util.ClassUtils;

public class ExecuteInfo implements Serializable{
	private volatile static Map<Class<? extends SQLFormat>, SQLFormat> sqlFormatMap = new HashMap<Class<? extends SQLFormat>, SQLFormat>();
	
	public static SQLFormat getSQLFormat(Class<? extends SQLFormat> sqlFormatClass){
		if(sqlFormatClass.getName().equals(MysqlFormat.class.getName())){
			return DB.DEFAULT_SQL_FORMAT;
		}
		
		SQLFormat sqlFormat = sqlFormatMap.get(sqlFormatClass);
		if(sqlFormat == null){
			synchronized(sqlFormatMap){
				sqlFormat = sqlFormatMap.get(sqlFormatClass);
				if(sqlFormat == null){
					try {
						sqlFormat = ClassUtils.newInstance(sqlFormatClass);
						sqlFormatMap.put(sqlFormatClass, sqlFormat);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return sqlFormat;
	}
	
	private static final long serialVersionUID = 1L;
	private Class<? extends AbstractDB> abstractDBClass;
	private Class<? extends SQLFormat> sqlFormatClass;
	private EOperationType operationType;
	private Collection<Object> beanList;
	
	public ExecuteInfo(){};
	
	public ExecuteInfo(Class<? extends AbstractDB> abstractDBClass, Class<? extends SQLFormat> sqlFormatClass, EOperationType operationType, Collection<Object> beans){
		this.abstractDBClass = abstractDBClass;
		this.sqlFormatClass = sqlFormatClass;
		this.operationType = operationType;
		this.beanList = beans;
	}
	
	public EOperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(EOperationType operationType) {
		this.operationType = operationType;
	}

	public Collection<Object> getBeanList() {
		return beanList;
	}

	public void setBeanList(Collection<Object> beanList) {
		this.beanList = beanList;
	}

	public Class<? extends AbstractDB> getAbstractDBClass() {
		return abstractDBClass;
	}

	public void setAbstractDBClass(Class<? extends AbstractDB> abstractDBClass) {
		this.abstractDBClass = abstractDBClass;
	}

	public Class<? extends SQLFormat> getSqlFormatClass() {
		return sqlFormatClass;
	}

	public void setSqlFormatClass(Class<? extends SQLFormat> sqlFormatClass) {
		this.sqlFormatClass = sqlFormatClass;
	}
	
	public SQLFormat getSQLFormat(){
		return getSQLFormat(sqlFormatClass);
	}
	
	public AbstractDB getAbstractDB(){
		return AbstractDB.getAbstractDB(abstractDBClass);
	}
}
