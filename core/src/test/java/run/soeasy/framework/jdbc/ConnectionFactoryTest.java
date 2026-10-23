package run.soeasy.framework.jdbc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.junit.Test;

import lombok.Data;

public class ConnectionFactoryTest {
	@Data
	private static class User{
		private Long id;
		private String name;
		private Integer age;
	}
	
	@Test
	public void test() throws SQLException, IOException {
		String createTableSql = "CREATE TABLE IF NOT EXISTS user (" + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL," + "age INTEGER)";
		ConnectionFactory connectionFactory = new SqlitConnectionFactory("jdbc:sqlite:embedded_sqlite.db");
		try {
			boolean executed = connectionFactory.newPipeline().prepareStatement(createTableSql).execute();
			System.out.println(executed);
			System.out.println("---------");
			int value = connectionFactory.newPipeline().prepareStatement("insert into user (name, age) values (?,?)").setParams("soeasy.run", 1).executeUpdate();
			System.out.println(value);
			String querySql = "select * from user";
			System.out.println("---------");
			System.out.println(connectionFactory.newPipeline().prepareStatement(querySql).query().map(JdbcUtils::getRowValueMap).optional().get());
			System.out.println("---------");
			connectionFactory.newPipeline().prepareStatement(querySql).query().rows((rs) -> {
				return JdbcUtils.getResultSetMapper().convert(rs, User.class);
			}).forEach((user) -> {
				assert user.getAge() == 1;
			});
		}finally {
			Files.deleteIfExists(Paths.get("embedded_sqlite.db"));
		}
	}
}
