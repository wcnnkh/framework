package scw.orm.sql;

import java.util.Collection;

import scw.core.instance.CannotInstantiateException;
import scw.core.instance.NoArgsInstanceFactory;
import scw.orm.FieldDefinitionFactory;
import scw.orm.Filter;
import scw.orm.GetterFilter;
import scw.orm.SetterFilter;

public class DefaultSqlMapper extends AbstractSqlMappingOperations implements SqlMapper {
	private TableNameMapping tableNameMapping;
	private FieldDefinitionFactory fieldDefinitionFactory;
	private Collection<? extends Filter> filters;
	private NoArgsInstanceFactory noArgsInstanceFactory;

	public DefaultSqlMapper(TableNameMapping tableNameMapping, FieldDefinitionFactory fieldDefinitionFactory,
			Collection<? extends Filter> filters, NoArgsInstanceFactory noArgsInstanceFactory) {
		this.tableNameMapping = tableNameMapping;
		this.fieldDefinitionFactory = fieldDefinitionFactory;
		this.filters = filters;
		this.noArgsInstanceFactory = noArgsInstanceFactory;
	}

	public String getTableName(Class<?> clazz) {
		return tableNameMapping.getTableName(clazz);
	}

	public FieldDefinitionFactory getFieldDefinitionFactory() {
		return fieldDefinitionFactory;
	}

	public <T> T newInstance(Class<T> type) {
		if (noArgsInstanceFactory.isInstance(type)) {
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
