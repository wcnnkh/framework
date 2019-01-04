package scw.db.sql;

import java.util.Collection;

import scw.beans.BeanFieldListen;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.database.SQL;
import scw.database.TableInfo;
import scw.db.PrimaryKeyParameter;
import scw.db.sql.mysql.CreateTableSQL;
import scw.db.sql.mysql.DecrSQL;
import scw.db.sql.mysql.DeleteSQL;
import scw.db.sql.mysql.IncrSQL;
import scw.db.sql.mysql.InsertSQL;
import scw.db.sql.mysql.SaveOrUpdateSQL;
import scw.db.sql.mysql.SaveOrUpdateSQLByBeanListen;
import scw.db.sql.mysql.SelectByIdSQL;
import scw.db.sql.mysql.SelectINId;
import scw.db.sql.mysql.UpdateSQL;
import scw.db.sql.mysql.UpdateSQLByBeanListen;

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

	public SQL toUpdateSql(Object obj, TableInfo tableInfo, String tableName) {
		try {
			if (obj instanceof BeanFieldListen) {
				return new UpdateSQLByBeanListen((BeanFieldListen) obj, tableInfo, tableName);
			} else {
				return new UpdateSQL(obj, tableInfo, tableName);
			}
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public SQL toSaveOrUpdateSql(Object obj, TableInfo tableInfo, String tableName) {
		try {
			if (obj instanceof BeanFieldListen) {
				return new SaveOrUpdateSQLByBeanListen((BeanFieldListen) obj, tableInfo, tableName);
			} else {
				return new SaveOrUpdateSQL(obj, tableInfo, tableName);
			}
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}

	}

	public SQL toIncrSql(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit,
			Double maxValue) {
		return new IncrSQL(obj, tableInfo, tableName, fieldName, limit, maxValue);
	}

	public SQL toDecrSql(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit,
			Double minValue) {
		try {
			return new DecrSQL(obj, tableInfo, tableName, fieldName, limit, minValue);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new ShuChaoWenRuntimeException();
	}

	public SQL toSelectINId(TableInfo tableInfo, String tableName,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		return new SelectINId(tableInfo, tableName, primaryKeyParameters);
	}
}