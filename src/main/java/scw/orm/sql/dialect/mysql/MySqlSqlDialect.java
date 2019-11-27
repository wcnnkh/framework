package scw.orm.sql.dialect.mysql;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import scw.core.FieldSetterListen;
import scw.core.Pagination;
import scw.orm.sql.SqlMappingOperations;
import scw.orm.sql.dialect.PaginationSql;
import scw.orm.sql.dialect.SqlDialect;
import scw.orm.sql.dialect.SqlDialectException;
import scw.sql.SimpleSql;
import scw.sql.Sql;

public class MySqlSqlDialect implements SqlDialect {
	public Sql toCreateTableSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName)
			throws SqlDialectException {
		return new CreateTableSql(sqlMappingOperations, clazz, tableName);
	}

	public Sql toInsertSql(SqlMappingOperations sqlMappingOperations, Object obj, Class<?> clazz, String tableName)
			throws SqlDialectException {
		try {
			return new InsertSQL(sqlMappingOperations, clazz, tableName, obj);
		} catch (Exception e) {
			throw new SqlDialectException(clazz.getName(), e);
		}
	}

	public Sql toUpdateSql(SqlMappingOperations sqlMappingOperations, Object obj, Class<?> clazz, String tableName)
			throws SqlDialectException {
		try {
			return (obj instanceof FieldSetterListen)
					? new UpdateSQLByBeanListen(sqlMappingOperations, clazz, (FieldSetterListen) obj, tableName)
					: new UpdateSQL(sqlMappingOperations, clazz, obj, tableName);
		} catch (Exception e) {
			throw new SqlDialectException(clazz.getName(), e);
		}
	}

	public Sql toSaveOrUpdateSql(SqlMappingOperations sqlMappingOperations, Object obj, Class<?> clazz,
			String tableName) throws SqlDialectException {
		try {
			return new SaveOrUpdateSQL(sqlMappingOperations, clazz, obj, tableName);
		} catch (Exception e) {
			throw new SqlDialectException(clazz.getName(), e);
		}
	}

	public Sql toDeleteSql(SqlMappingOperations sqlMappingOperations, Object obj, Class<?> clazz, String tableName)
			throws SqlDialectException {
		try {
			return new DeleteSQL(sqlMappingOperations, clazz, obj, tableName);
		} catch (Exception e) {
			throw new SqlDialectException(clazz.getName(), e);
		}
	}

	public Sql toDeleteByIdSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName,
			Object[] parimayKeys) throws SqlDialectException {
		try {
			return new DeleteByIdSql(sqlMappingOperations, clazz, tableName, parimayKeys);
		} catch (Exception e) {
			throw new SqlDialectException(clazz.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public Sql toSelectByIdSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName,
			Object[] params) throws SqlDialectException {
		try {
			return new SelectByIdSQL(sqlMappingOperations, clazz, tableName,
					params == null ? Collections.EMPTY_LIST : Arrays.asList(params));
		} catch (Exception e) {
			throw new SqlDialectException(clazz.getName(), e);
		}
	}

	public Sql toSelectInIdSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName,
			Object[] params, Collection<?> inIdList) throws SqlDialectException {
		try {
			return new SelectInIdSQL(sqlMappingOperations, clazz, tableName, params, inIdList);
		} catch (Exception e) {
			throw new SqlDialectException(clazz.getName(), e);
		}
	}

	public PaginationSql toPaginationSql(SqlMappingOperations sqlMappingOperations, Sql sql, long page, int limit)
			throws SqlDialectException {
		String str = sql.getSql();
		int fromIndex = str.indexOf(" from ");// ignore select
		if (fromIndex == -1) {
			fromIndex = str.indexOf(" FROM ");
		}

		if (fromIndex == -1) {
			throw new IndexOutOfBoundsException(str);
		}

		String whereSql;
		int orderIndex = str.lastIndexOf(" order by ");
		if (orderIndex == -1) {
			orderIndex = str.lastIndexOf(" ORDER BY ");
		}

		if (orderIndex == -1) {// 不存在 order by 子语句
			whereSql = str.substring(fromIndex);
		} else {
			whereSql = str.substring(fromIndex, orderIndex);
		}

		Sql countSql = new SimpleSql("select count(*)" + whereSql, sql.getParams());
		StringBuilder sb = new StringBuilder(str);
		sb.append(" limit ").append(Pagination.getBegin(page, limit)).append(",").append(limit);
		return new PaginationSql(countSql, new SimpleSql(sb.toString(), sql.getParams()));
	}

	public Sql toCopyTableStructureSql(SqlMappingOperations sqlMappingOperations, String newTableName,
			String oldTableName) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `").append(newTableName).append("`");
		sb.append(" like `").append(oldTableName).append("`");
		return new SimpleSql(sb.toString());
	}

	private static final String LAST_INSERT_ID_SQL = "select last_insert_id()";

	public Sql toLastInsertIdSql(SqlMappingOperations sqlMappingOperations, String tableName)
			throws SqlDialectException {
		return new SimpleSql(LAST_INSERT_ID_SQL);
	}

	public Sql toMaxIdSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName, String idField)
			throws SqlDialectException {
		try {
			return new MaxIdSql(sqlMappingOperations, clazz, tableName, idField);
		} catch (Exception e) {
			throw new SqlDialectException(clazz.getName(), e);
		}
	}

}
