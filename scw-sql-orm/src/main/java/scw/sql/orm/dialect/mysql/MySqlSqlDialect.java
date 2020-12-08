package scw.sql.orm.dialect.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.sql.orm.dialect.AbstractSqlDialect;
import scw.sql.orm.dialect.DialectHelper;
import scw.sql.orm.dialect.SqlDialectException;
import scw.sql.orm.dialect.TableStructureMapping;

public class MySqlSqlDialect extends AbstractSqlDialect {

	public MySqlSqlDialect() {
		this(new DialectHelper());
	}

	public MySqlSqlDialect(DialectHelper dialectHelper) {
		super(dialectHelper);
	}

	public Sql toCreateTableSql(Class<?> clazz, String tableName) throws SqlDialectException {
		return new CreateTableSql(clazz, tableName, getDialectHelper());
	}

	public Sql toSaveOrUpdateSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException {
		return new SaveOrUpdateSQL(clazz, obj, tableName, getDialectHelper());
	}

	private static final String LAST_INSERT_ID_SQL = "select last_insert_id()";

	public Sql toLastInsertIdSql(String tableName) throws SqlDialectException {
		return new SimpleSql(LAST_INSERT_ID_SQL);
	}

	public Sql toMaxIdSql(Class<?> clazz, String tableName, String idField) throws SqlDialectException {
		return new MaxIdSql(clazz, tableName, idField, getDialectHelper());
	}

	public TableStructureMapping getTableStructureMapping(Class<?> clazz, final String tableName) {
		return new TableStructureMapping() {
			
			public Sql getSql() {
				return new SimpleSql("select * from INFORMATION_SCHEMA.COLUMNS where table_schema=database() and table_name=?", tableName);
			}
			
			public String getName(ResultSet resultSet) throws SQLException {
				return resultSet.getString("COLUMN_NAME");
			}
		};
	}

	public Sql toCopyTableStructureSql(String newTableName, String oldTableName) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append(getDialectHelper().getCreateTablePrefix());
		sb.append(" ");
		getDialectHelper().keywordProcessing(sb, newTableName);
		sb.append(" like ");
		getDialectHelper().keywordProcessing(sb, oldTableName);
		return new SimpleSql(sb.toString());
	}
}
