package run.soeasy.framework.data.jdbc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.junit.Test;

import lombok.Data;
import run.soeasy.framework.core.util.RandomUtils;
import run.soeasy.framework.data.jdbc.repository.JdbcTemplate;
import run.soeasy.framework.data.orm.annotation.Entity;
import run.soeasy.framework.data.orm.annotation.PrimaryKey;

public class ConnectionFactoryTest {
	@Data
	@Entity
	private static class User {
		@PrimaryKey
		private Long id;
		private String userName;
		private Integer age;
	}

	@Test
	public void test() throws SQLException, IOException {
		String createTableSql = "CREATE TABLE IF NOT EXISTS user (" + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "user_name TEXT NOT NULL," + "age INTEGER)";
		ConnectionFactory connectionFactory = new SqlitConnectionFactory("jdbc:sqlite:embedded_sqlite.db");
		try {
			boolean executed = connectionFactory.newPipeline().prepareStatement(createTableSql).execute();
			System.out.println(executed);
			System.out.println("---------");
			int value = connectionFactory.newPipeline().prepareStatement("insert into user (name, age) values (?,?)")
					.setParams("soeasy.run", 1).executeUpdate();
			System.out.println(value);
			String querySql = "select * from user";
			System.out.println("---------");
			System.out.println(connectionFactory.newPipeline().prepareStatement(querySql).query()
					.map(JdbcUtils::getRowValueMap).optional().get());
			System.out.println("---------");

			JdbcTemplate jdbcTemplate = new JdbcTemplate(connectionFactory);
			User entity = new User();
			entity.setAge(1);
			entity.setId(System.currentTimeMillis());
			entity.setUserName(RandomUtils.randomString(10));
			jdbcTemplate.insert(User.class, entity);
			jdbcTemplate.queryAll(User.class).forEach((user) -> {
				User result = jdbcTemplate.query(User.class, user).getFirst();
				assert result.equals(user);
				jdbcTemplate.update(User.class, result);
				System.out.println(user);
				assert user.getAge() == 1;

				jdbcTemplate.delete(User.class, user);
			});
			System.out.println("---------");
		} finally {
			Files.deleteIfExists(Paths.get("embedded_sqlite.db"));
		}
	}
}
