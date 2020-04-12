package scw.orm.sql.support;

import scw.core.instance.annotation.Configuration;
import scw.orm.sql.SqlColumnFactory;
import scw.orm.support.CacheColumnFactoryWrapper;

@Configuration(order=Integer.MIN_VALUE)
public class DefaultSqlColumnFactory extends CacheColumnFactoryWrapper implements
		SqlColumnFactory {

	public DefaultSqlColumnFactory() {
		super(new TableColumnFactory());
	}
}
