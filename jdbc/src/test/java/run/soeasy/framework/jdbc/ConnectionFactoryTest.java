package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class ConnectionFactoryTest {
	@Test
	public void test() throws SQLException {
		String createTableSql = "CREATE TABLE IF NOT EXISTS user (" + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL," + "age INTEGER)";
		ConnectionFactory connectionFactory = new SqlitConnectionFactory("jdbc:sqlite:embedded_sqlite.db");
		boolean executed = connectionFactory.newPipeline().prepareStatement(createTableSql).execute();
		System.out.println(executed);
		int value = connectionFactory.newPipeline().prepareStatement("insert into user (name, age) values (?,?)").setParams("soeasy.run", 1).executeUpdate();
		System.out.println(value);
		ResultSet resultSet = connectionFactory.newPipeline().prepareStatement("select * from user").query().get();
		System.out.println(resultSet.isClosed());
		System.out.println(JdbcUtils.getRowValueMap(resultSet));
		resultSet.close();
	}
}
