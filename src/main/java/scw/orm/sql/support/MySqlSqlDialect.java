package scw.orm.sql.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import scw.core.FieldSetterListen;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.dialect.AbstractSqlDialect;
import scw.orm.sql.dialect.SqlDialectException;
import scw.orm.sql.dialect.mysql.CreateTableSql;
import scw.orm.sql.dialect.mysql.DeleteByIdSql;
import scw.orm.sql.dialect.mysql.DeleteSQL;
import scw.orm.sql.dialect.mysql.InsertSQL;
import scw.orm.sql.dialect.mysql.MaxIdSql;
import scw.orm.sql.dialect.mysql.SaveOrUpdateSQL;
import scw.orm.sql.dialect.mysql.SelectByIdSQL;
import scw.orm.sql.dialect.mysql.SelectInIdSQL;
import scw.orm.sql.dialect.mysql.UpdateSQL;
import scw.orm.sql.dialect.mysql.UpdateSQLByBeanListen;
import scw.sql.SimpleSql;
import scw.sql.Sql;

public class MySqlSqlDialect extends AbstractSqlDialect {
	public SqlMapper getSqlMapper() {
		return SqlORMUtils.getSqlMapper();
	}

	public Sql toCreateTableSql(Class<?> clazz, String tableName) throws SqlDialectException {
		return new CreateTableSql(getSqlMapper(), clazz, tableName);
	}

	public Sql toInsertSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException {
		try {
			return new InsertSQL(getSqlMapper(), clazz, tableName, obj);
		} catch (Exception e) {
			throw new SqlDialectException(clazz.getName(), e);
		}
	}

	public Sql toUpdateSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException {
		return (obj instanceof FieldSetterListen)
				? new UpdateSQLByBeanListen(getSqlMapper(), clazz, (FieldSetterListen) obj, tableName)
				: new UpdateSQL(getSqlMapper(), clazz, obj, tableName);
	}

	public Sql toSaveOrUpdateSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException {
		return new SaveOrUpdateSQL(getSqlMapper(), clazz, obj, tableName);
	}

	public Sql toDeleteSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException {
		return new DeleteSQL(getSqlMapper(), clazz, obj, tableName);
	}

	public Sql toDeleteByIdSql(Class<?> clazz, String tableName, Object[] parimayKeys) throws SqlDialectException {
		return new DeleteByIdSql(getSqlMapper(), clazz, tableName, parimayKeys);
	}

	@SuppressWarnings("unchecked")
	public Sql toSelectByIdSql(Class<?> clazz, String tableName, Object[] params) throws SqlDialectException {
		return new SelectByIdSQL(getSqlMapper(), clazz, tableName,
				params == null ? Collections.EMPTY_LIST : Arrays.asList(params));
	}

	public Sql toSelectInIdSql(Class<?> clazz, String tableName, Object[] params, Collection<?> inIdList)
			throws SqlDialectException {
		return new SelectInIdSQL(getSqlMapper(), clazz, tableName, params, inIdList);
	}

	private static final String LAST_INSERT_ID_SQL = "select last_insert_id()";

	public Sql toLastInsertIdSql(String tableName) throws SqlDialectException {
		return new SimpleSql(LAST_INSERT_ID_SQL);
	}

	public Sql toMaxIdSql(Class<?> clazz, String tableName, String idField) throws SqlDialectException {
		return new MaxIdSql(getSqlMapper(), clazz, tableName, idField);
	}

}
