package scw.db;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

import scw.db.sql.SQLFormat;

public class DataSource extends DB{
	private final javax.sql.DataSource dataSource;
	
	public DataSource(javax.sql.DataSource dataSource){
		this(dataSource, null);
	}
	
	public DataSource(javax.sql.DataSource dataSource, SQLFormat sqlFormat){
		super(sqlFormat);
		this.dataSource = dataSource;
	}
	
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public void close() throws Exception {
		if(dataSource instanceof Closeable){
			((Closeable) dataSource).close();
		}
	}
}
