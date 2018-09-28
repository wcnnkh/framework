package shuchaowen.core.db.sql.format.mysql;

import shuchaowen.core.db.DB;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.TableName;
import shuchaowen.core.db.proxy.BeanProxy;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class MysqlFormat implements SQLFormat {
	public SQL toCreateTableSql(TableInfo tableInfo, String tableName) {
		return new CreateTableSQL(tableInfo, tableName);
	}

	public SQL toSelectByIdSql(TableInfo info, String tableName, Object... ids) {
		return new SelectByIdSQL(info, tableName, ids);
	}

	public SQL toInsertSql(Object obj, TableInfo tableInfo, String tableName) {
		return new InsertSQL(tableInfo, tableName, obj);
	}

	public SQL toDeleteSql(Object obj, TableInfo tableInfo, String tableName) {
		return new DeleteSQL(obj, tableInfo, tableName);
	}

	public SQL toMaxValueSQL(TableInfo tableInfo, String tableName, String fieldName) {
		return new MaxValueSQL(tableInfo, tableName, fieldName);
	}

	public SQL toUpdateSql(Object obj, TableInfo tableInfo, String tableName) {
		try {
			if(obj instanceof BeanProxy){
				return new UpdateSQLByBeanProxy((BeanProxy)obj, tableInfo, tableName);
			}else{
				return new UpdateSQL(obj, tableInfo, tableName);
			}
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public SQL toInsertSql(Object obj) {
		TableInfo tableInfo = DB.getTableInfo(obj.getClass());
		String tableName = (obj instanceof TableName)? ((TableName)obj).tableName():tableInfo.getName();
		return toInsertSql(obj, tableInfo, tableName);
	}

	public SQL toUpdateSql(Object obj) {
		TableInfo tableInfo = DB.getTableInfo(obj.getClass());
		String tableName = (obj instanceof TableName)? ((TableName)obj).tableName():tableInfo.getName();
		return toUpdateSql(obj, tableInfo, tableName);
	}
	
	public SQL toSaveOrUpdateSql(Object obj){
		TableInfo tableInfo = DB.getTableInfo(obj.getClass());
		String tableName = (obj instanceof TableName)? ((TableName)obj).tableName():tableInfo.getName();
		return toSaveOrUpdateSql(obj, tableInfo, tableName);
	}
	
	public SQL toSaveOrUpdateSql(Object obj, TableInfo tableInfo, String tableName){
		try {
			if(obj instanceof BeanProxy){
				return new SaveOrUpdateSQLByBeanProxy((BeanProxy)obj, tableInfo, tableName);
			}else{
				return new SaveOrUpdateSQL(obj, tableInfo, tableName);
			}
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		
	}

	public SQL toDeleteSql(Object obj) {
		TableInfo tableInfo = DB.getTableInfo(obj.getClass());
		String tableName = (obj instanceof TableName)? ((TableName)obj).tableName():tableInfo.getName();
		return toDeleteSql(obj, tableInfo, tableName);
	}

	public SQL toIncrSql(Object obj, String fieldName, double limit, Double maxValue) {
		TableInfo tableInfo = DB.getTableInfo(obj.getClass());
		String tableName = (obj instanceof TableName)? ((TableName)obj).tableName():tableInfo.getName();
		return toIncrSql(obj, tableInfo, tableName, fieldName, limit, maxValue);
	}

	public SQL toIncrSql(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit,
			Double maxValue) {
		return new IncrSQL(obj, tableInfo, tableName, fieldName, limit, maxValue);
	}

	public SQL toDecrSql(Object obj, String fieldName, double limit, Double minValue) {
		TableInfo tableInfo = DB.getTableInfo(obj.getClass());
		String tableName = (obj instanceof TableName)? ((TableName)obj).tableName():tableInfo.getName();
		return toDecrSql(obj, tableInfo, tableName, fieldName, limit, minValue);
	}

	public SQL toDecrSql(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit,
			Double minValue) {
		return new DecrSQL(obj, tableInfo, tableName, fieldName, limit, minValue);
	}
}