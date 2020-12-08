package scw.sqlite;

import scw.db.ConfigurableDB;
import scw.sql.orm.cache.DefaultCacheManager;
import scw.sql.orm.support.generation.DefaultGeneratorService;

public abstract class AbstractSQLiteDB extends ConfigurableDB{
	
	public AbstractSQLiteDB(){
		setCacheManager(new DefaultCacheManager());
		setGeneratorService(new DefaultGeneratorService());
		setSqlDialect(new SQLiteSqlDialect());
	}
}
