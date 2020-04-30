package scw.orm.sql.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import scw.aop.support.FieldSetterListen;
import scw.core.utils.CollectionUtils;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.SqlORMUtils;
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
import scw.orm.sql.enums.TableStructureResultField;
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

	public Sql toSelectInIdSql(Class<?> clazz, String tableName, Object[] ids, Collection<?> inIdList)
			throws SqlDialectException {
		return new SelectInIdSQL(getSqlMapper(), clazz, tableName, ids, inIdList);
	}

	private static final String LAST_INSERT_ID_SQL = "select last_insert_id()";

	public Sql toLastInsertIdSql(String tableName) throws SqlDialectException {
		return new SimpleSql(LAST_INSERT_ID_SQL);
	}

	public Sql toMaxIdSql(Class<?> clazz, String tableName, String idField) throws SqlDialectException {
		return new MaxIdSql(getSqlMapper(), clazz, tableName, idField);
	}

	protected String getTableStructureField(TableStructureResultField field) {
		switch (field) {
		case NAME:
			return "COLUMN_NAME";
		default:
			return null;
		}
	}

	public Sql toTableStructureSql(Class<?> clazz, String tableName,
			Collection<TableStructureResultField> fields) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		if (CollectionUtils.isEmpty(fields)) {
			sb.append("*");
		} else {
			Iterator<TableStructureResultField> iterator = fields.iterator();
			while (iterator.hasNext()) {
				String name = getTableStructureField(iterator.next());
				if (name == null) {
					continue;
				}

				sb.append(name);
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
		}
		sb.append(" from INFORMATION_SCHEMA.COLUMNS where table_schema=database() and table_name=?");
		return new SimpleSql(sb.toString(), getTableName(clazz, tableName));
	}
}
