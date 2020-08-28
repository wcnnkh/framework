package scw.db.pool;

import javax.sql.DataSource;

import scw.core.instance.InstanceUtils;

public interface DataSourceCreator {
	static final DataSourceCreator CREATOR = InstanceUtils.loadService(DataSourceCreator.class,
			"scw.db.pool.DruidDataSourceCreator", "scw.db.pool.HikaricpDataSourceCreator");

	DataSource create(Config config);
}
