package scw.orm.sql;

import java.util.Collection;
import java.util.Map;

import scw.core.instance.CannotInstantiateException;
import scw.core.instance.NoArgsInstanceFactory;
import scw.orm.Column;
import scw.orm.ColumnFactory;
import scw.orm.Filter;
import scw.orm.GetterFilter;
import scw.orm.ORMUtils;
import scw.orm.SetterFilter;

public class DefaultSqlMapper extends AbstractSqlMapper implements SqlMapper {
	private TableNameMapping tableNameMapping;
	private ColumnFactory columnFactory;
	private Collection<? extends Filter> filters;
	private NoArgsInstanceFactory noArgsInstanceFactory;

	public DefaultSqlMapper(TableNameMapping tableNameMapping, ColumnFactory columnFactory,
			Collection<? extends Filter> filters, NoArgsInstanceFactory noArgsInstanceFactory) {
		this.tableNameMapping = tableNameMapping;
		this.columnFactory = columnFactory;
		this.filters = filters;
		this.noArgsInstanceFactory = noArgsInstanceFactory;
	}

	@Override
	public char getPrimaryKeyConnectorCharacter() {
		return ORMUtils.PRIMARY_KEY_CONNECTOR_CHARACTER;
	}

	public String getTableName(Class<?> clazz) {
		return tableNameMapping.getTableName(clazz);
	}

	public Map<String, Column> getColumnMap(Class<?> clazz) {
		return columnFactory.getColumnMap(clazz);
	}

	public <T> T newInstance(Class<T> type) {
		if (!noArgsInstanceFactory.isInstance(type)) {
			throw new CannotInstantiateException("Cannot instantiate [" + type.getName() + "]");
		}
		return noArgsInstanceFactory.getInstance(type);
	}

	@Override
	public Collection<? extends SetterFilter> getSetterFilters() {
		return filters;
	}

	@Override
	public Collection<? extends GetterFilter> getGetterFilters() {
		return filters;
	}
}
