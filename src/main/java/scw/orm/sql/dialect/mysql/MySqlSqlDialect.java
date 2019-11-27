package scw.orm.sql.dialect.mysql;

import java.util.Collection;

import scw.core.FieldSetterListen;
import scw.core.Pagination;
import scw.orm.MappingOperations;
import scw.orm.sql.dialect.PaginationSql;
import scw.orm.sql.dialect.SqlDialect;
import scw.sql.SimpleSql;
import scw.sql.Sql;

public class MySqlSqlDialect implements SqlDialect {
	private MappingOperations mappingOperations;

	public MySqlSqlDialect(MappingOperations mappingOperations) {
		this.mappingOperations = mappingOperations;
	}

	public Sql toCreateTableSql(Class<?> clazz, String tableName) throws Exception {
		return new CreateTableSql(mappingOperations, clazz, tableName);
	}

	public Sql toInsertSql(Object obj, Class<?> clazz, String tableName) throws Exception {
		return new InsertSQL(mappingOperations, clazz, tableName, obj);
	}

	public Sql toUpdateSql(Object obj, Class<?> clazz, String tableName) throws Exception {
		return (obj instanceof FieldSetterListen)
				? new UpdateSQLByBeanListen(mappingOperations, clazz, (FieldSetterListen) obj, tableName)
				: new UpdateSQL(mappingOperations, clazz, obj, tableName);
	}

	public Sql toSaveOrUpdateSql(Object obj, Class<?> clazz, String tableName) throws Exception {
		return new SaveOrUpdateSQL(mappingOperations, clazz, obj, tableName);
	}

	public Sql toDeleteSql(Object obj, Class<?> clazz, String tableName) throws Exception {
		return new DeleteSQL(mappingOperations, clazz, obj, tableName);
	}

	public Sql toDeleteByIdSql(Class<?> clazz, String tableName, Object[] parimayKeys) throws Exception {
		return new DeleteByIdSql(mappingOperations, clazz, tableName, parimayKeys);
	}

	public Sql toSelectByIdSql(Class<?> clazz, String tableName, Object[] params) throws Exception {
		return new SelectByIdSQL(mappingOperations, clazz, tableName, params);
	}

	public Sql toSelectInIdSql(Class<?> clazz, String tableName, Object[] params, Collection<?> inIdList)
			throws Exception {
		return new SelectInIdSQL(mappingOperations, clazz, tableName, params, inIdList);
	}

	public PaginationSql toPaginationSql(Sql sql, long page, int limit) throws Exception {
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

	public Sql toCopyTableStructureSql(String newTableName, String oldTableName) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `").append(newTableName).append("`");
		sb.append(" like `").append(oldTableName).append("`");
		return new SimpleSql(sb.toString());
	}

	private static final String LAST_INSERT_ID_SQL = "select last_insert_id()";

	public Sql toLastInsertIdSql(String tableName) throws Exception {
		return new SimpleSql(LAST_INSERT_ID_SQL);
	}

	public Sql toMaxIdSql(Class<?> clazz, String tableName, String idField) throws Exception {
		return new MaxIdSql(mappingOperations, clazz, tableName, idField);
	}

}
