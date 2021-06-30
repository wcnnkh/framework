package scw.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.sqlite.SQLiteDataSource;

import scw.sql.ConnectionFactory;

public class SQLiteConnectionFactory implements ConnectionFactory {
	private final SQLiteDataSource dataSource;

	public SQLiteConnectionFactory(SQLiteDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public SQLiteConnectionFactory(String databasePath) {
		File file = new File(databasePath);
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
		}
		this.dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:" + databasePath);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public SQLiteDataSource getDataSource() {
		return dataSource;
	}
}
