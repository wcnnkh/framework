package io.basc.framework.druid;

import com.alibaba.druid.pool.DruidDataSource;

import io.basc.framework.jdbc.template.Database;
import io.basc.framework.jdbc.template.DatabaseDialect;

public class DruidDatabase extends Database {

	public DruidDatabase(DruidDataSource druidDataSource, DatabaseDialect databaseDialect) {
		this(new DruidConnectionFactory(druidDataSource, databaseDialect));
	}

	public DruidDatabase(DruidConnectionFactory druidConnectionFactory) {
		super(druidConnectionFactory);
	}

}
