package scw.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import scw.sql.DriverManagerConnectionFactory;

public class LocalSQLiteDB extends AbstractSQLiteDB {
	private DriverManagerConnectionFactory connectionFactory;

	public LocalSQLiteDB(String databasePath) {
		File file = new File(databasePath);
		if(!file.exists()){
			File parent = file.getParentFile();
			if(parent != null && !parent.exists()){
				parent.mkdirs();
			}
		}
		this.connectionFactory = new DriverManagerConnectionFactory(
				"jdbc:sqlite:" + databasePath);
	}

	public Connection getConnection() throws SQLException {
		return connectionFactory.getConnection();
	}
}
