package scw.sql.orm.dialect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import scw.aop.support.FieldSetterListen;
import scw.core.utils.CollectionUtils;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.sql.orm.dialect.mysql.CreateTableSql;
import scw.sql.orm.dialect.mysql.DeleteByIdSql;
import scw.sql.orm.dialect.mysql.DeleteSQL;
import scw.sql.orm.dialect.mysql.InsertSQL;
import scw.sql.orm.dialect.mysql.MaxIdSql;
import scw.sql.orm.dialect.mysql.SaveOrUpdateSQL;
import scw.sql.orm.dialect.mysql.SelectByIdSQL;
import scw.sql.orm.dialect.mysql.SelectInIdSQL;
import scw.sql.orm.dialect.mysql.UpdateSQL;
import scw.sql.orm.dialect.mysql.UpdateSQLByBeanListen;
import scw.sql.orm.enums.TableStructureResultField;

public class MySqlSqlDialect extends AbstractSqlDialect {

	public Sql toCreateTableSql(Class<?> clazz, String tableName) throws SqlDialectException {
		return new CreateTableSql(getObjectRelationalMapping(), clazz, tableName, getSqlTypeFactory());
	}

	public Sql toInsertSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException {
		return new InsertSQL(getObjectRelationalMapping(), clazz, tableName, obj);
	}

	public Sql toUpdateSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException {
		return (obj instanceof FieldSetterListen)
				? new UpdateSQLByBeanListen(getObjectRelationalMapping(), clazz, (FieldSetterListen) obj, tableName)
				: new UpdateSQL(getObjectRelationalMapping(), clazz, obj, tableName);
	}

	public Sql toSaveOrUpdateSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException {
		return new SaveOrUpdateSQL(getObjectRelationalMapping(), clazz, obj, tableName);
	}

	public Sql toDeleteSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException {
		return new DeleteSQL(getObjectRelationalMapping(), clazz, obj, tableName);
	}

	public Sql toDeleteByIdSql(Class<?> clazz, String tableName, Object[] parimayKeys) throws SqlDialectException {
		return new DeleteByIdSql(getObjectRelationalMapping(), clazz, tableName, parimayKeys);
	}

	@SuppressWarnings("unchecked")
	public Sql toSelectByIdSql(Class<?> clazz, String tableName, Object[] params) throws SqlDialectException {
		return new SelectByIdSQL(getObjectRelationalMapping(), clazz, tableName,
				params == null ? Collections.EMPTY_LIST : Arrays.asList(params));
	}

	public Sql toSelectInIdSql(Class<?> clazz, String tableName, Object[] ids, Collection<?> inIdList)
			throws SqlDialectException {
		return new SelectInIdSQL(getObjectRelationalMapping(), clazz, tableName, ids, inIdList);
	}

	private static final String LAST_INSERT_ID_SQL = "select last_insert_id()";

	public Sql toLastInsertIdSql(String tableName) throws SqlDialectException {
		return new SimpleSql(LAST_INSERT_ID_SQL);
	}

	public Sql toMaxIdSql(Class<?> clazz, String tableName, String idField) throws SqlDialectException {
		return new MaxIdSql(getObjectRelationalMapping(), clazz, tableName, idField);
	}

	protected String getTableStructureField(TableStructureResultField field) {
		switch (field) {
		case NAME:
			return "COLUMN_NAME";
		default:
			return null;
		}
	}

	public Sql toTableStructureSql(Class<?> clazz, String tableName, Collection<TableStructureResultField> fields) {
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
		return new SimpleSql(sb.toString(), tableName);
	}
}
