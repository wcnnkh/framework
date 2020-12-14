package scw.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.sqlite.SQLiteDataSource;

import scw.db.ConfigurableDB;
import scw.sql.orm.cache.DefaultCacheManager;
import scw.sql.orm.support.generation.DefaultGeneratorService;

public class SQLiteDB extends ConfigurableDB{
	private SQLiteDataSource dataSource;
	
	public SQLiteDB(SQLiteDataSource dataSource){
		preInit();
		this.dataSource = dataSource;
	}
	
	private void preInit(){
		setCacheManager(new DefaultCacheManager());
		setGeneratorService(new DefaultGeneratorService());
		setSqlDialect(new SQLiteSqlDialect());
	}
	
	public SQLiteDB(String databasePath){
		preInit();
		File file = new File(databasePath);
		if(!file.exists()){
			File parent = file.getParentFile();
			if(parent != null && !parent.exists()){
				parent.mkdirs();
			}
		}
		this.dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:" + databasePath);
	}
	
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
