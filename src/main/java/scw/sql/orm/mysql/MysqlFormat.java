package scw.sql.orm.mysql;

import java.util.Collection;

import scw.core.FieldSetterListen;
import scw.core.Pagination;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.sql.orm.PaginationSql;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.TableInfo;

public final class MysqlFormat implements SqlFormat {
	public Sql toCreateTableSql(TableInfo tableInfo, String tableName) {
		return new CreateTableSQL(tableInfo, tableName);
	}

	public Sql toSelectByIdSql(TableInfo info, String tableName, Object[] ids) {
		try {
			return new SelectByIdSQL(info, tableName, ids);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Sql toInsertSql(Object obj, TableInfo tableInfo, String tableName) {
		return new InsertSQL(tableInfo, tableName, obj);
	}

	public Sql toDeleteSql(Object obj, TableInfo tableInfo, String tableName) {
		try {
			return new DeleteSQL(obj, tableInfo, tableName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Sql toUpdateSql(Object obj, TableInfo tableInfo, String tableName) {
		try {
			if (obj instanceof FieldSetterListen) {
				return new UpdateSQLByBeanListen((FieldSetterListen) obj, tableInfo, tableName);
			} else {
				return new UpdateSQL(obj, tableInfo, tableName);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Sql toSaveOrUpdateSql(Object obj, TableInfo tableInfo, String tableName) {
		try {
			return new SaveOrUpdateSQL(obj, tableInfo, tableName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Sql toDeleteByIdSql(TableInfo tableInfo, String tableName, Object[] parimayKeys) {
		return new DeleteSQL(tableInfo, tableName, parimayKeys);
	}

	public PaginationSql toPaginationSql(Sql sql, long page, int limit) {
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

	public Sql toSelectInIdSql(TableInfo tableInfo, String tableName, Object[] params, Collection<?> inIdList) {
		return new SelectInIdSQL(tableInfo, tableName, params, inIdList);
	}

	public Sql toCopyTableStructureSql(String newTableName, String oldTableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `").append(newTableName).append("`");
		sb.append(" like `").append(oldTableName).append("`");
		return new SimpleSql(sb.toString());
	}

	private static final String LAST_INSERT_ID_SQL = "select last_insert_id()";

	public Sql toLastInsertIdSql(String tableName) {
		return new SimpleSql(LAST_INSERT_ID_SQL);
	}

	public Sql toMaxIdSql(TableInfo tableInfo, String tableName, String idField) {
		return new MaxIdSql(tableInfo, tableName, idField);
	}
}