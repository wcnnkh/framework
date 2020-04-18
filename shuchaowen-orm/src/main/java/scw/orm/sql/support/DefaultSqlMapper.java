package scw.orm.sql.support;

import java.util.Collection;
import java.util.Map;

import scw.core.instance.annotation.Configuration;
import scw.orm.Column;
import scw.orm.Filter;
import scw.orm.GetterFilter;
import scw.orm.ORMInstanceFactory;
import scw.orm.ORMUtils;
import scw.orm.SetterFilter;
import scw.orm.sql.AbstractSqlMapper;
import scw.orm.sql.SqlColumnFactory;
import scw.orm.sql.SqlFilter;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.SqlORMUtils;
import scw.orm.sql.TableNameMapping;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultSqlMapper extends AbstractSqlMapper implements SqlMapper {
	private TableNameMapping tableNameMapping;
	private SqlColumnFactory sqlColumnFactory;
	private Collection<? extends Filter> sqlFilters;
	private ORMInstanceFactory instanceFactory;

	public DefaultSqlMapper() {
		this(SqlORMUtils.getTableNameMapping(), SqlORMUtils
				.getSqlColumnFactory(), SqlORMUtils.getSqlFilters(), ORMUtils
				.getInstanceFactory());
	}

	public DefaultSqlMapper(TableNameMapping tableNameMapping,
			SqlColumnFactory sqlColumnFactory,
			Collection<? extends SqlFilter> sqlFilters,
			ORMInstanceFactory instanceFactory) {
		this.tableNameMapping = tableNameMapping;
		this.sqlColumnFactory = sqlColumnFactory;
		this.sqlFilters = sqlFilters;
		this.instanceFactory = instanceFactory;
	}

	public TableNameMapping getTableNameMapping() {
		return tableNameMapping;
	}

	@Override
	public char getPrimaryKeyConnectorCharacter() {
		return ORMUtils.PRIMARY_KEY_CONNECTOR_CHARACTER;
	}

	public String getTableName(Class<?> clazz) {
		return tableNameMapping.getTableName(clazz);
	}

	public Map<String, ? extends Column> getColumnMap(Class<?> clazz) {
		return sqlColumnFactory.getColumnMap(clazz);
	}

	public ORMInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	@Override
	public Collection<? extends SetterFilter> getSetterFilters() {
		return sqlFilters;
	}

	@Override
	public Collection<? extends GetterFilter> getGetterFilters() {
		return sqlFilters;
	}
}
