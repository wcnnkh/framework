package scw.sqlite;

import java.sql.ResultSet;
import java.sql.SQLException;

import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.sql.orm.dialect.TableStructureMapping;
import scw.sql.orm.dialect.mysql.MySqlSqlDialect;

public class SQLiteSqlDialect extends MySqlSqlDialect{
	
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
