package scw.sqlite;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.orm.sql.SqlDialectException;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.sql.orm.dialect.TableStructureMapping;
import scw.sql.orm.dialect.mysql.MySqlSqlDialect;

public class SQLiteSqlDialect extends MySqlSqlDialect{
	
	@Override
	public Sql toCreateTableSql(Class<?> clazz, String tableName)
			throws SqlDialectException {
		return new CreateTableSql(clazz, tableName, getDialectHelper());
	}
	
	@Override
	public Sql toLastInsertIdSql(String tableName) throws SqlDialectException {
		return new SimpleSql("SELECT last_insert_rowid()");
	}
	
	@Override
	public Sql toSaveOrUpdateSql(Object obj, Class<?> clazz, String tableName)
			throws SqlDialectException {
		return new ReplaceSql(clazz, obj, tableName, getDialectHelper());
	}
	
	@Override
	public TableStructureMapping getTableStructureMapping(Class<?> clazz,
			final String tableName) {
		return new TableStructureMapping() {
			
			public Sql getSql() {
				return new SimpleSql("pragma table_info(" + tableName + ")");
			}
			
			public String getName(ResultSet resultSet) throws SQLException {
				return resultSet.getString("name");
			}
		};
	}
}
