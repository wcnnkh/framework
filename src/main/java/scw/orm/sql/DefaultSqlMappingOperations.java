package scw.orm.sql;

import java.util.Collection;

import scw.core.instance.NoArgsInstanceFactory;
import scw.core.utils.StringUtils;
import scw.orm.DefaultMappingOperations;
import scw.orm.FieldDefinitionFactory;
import scw.orm.GetterFilter;
import scw.orm.SetterFilter;
import scw.sql.orm.annotation.Table;

public class DefaultSqlMappingOperations extends DefaultMappingOperations implements SqlMappingOperations {

	public DefaultSqlMappingOperations(FieldDefinitionFactory fieldDefinitionFactory,
			Collection<SetterFilter> setterFilters, Collection<GetterFilter> getterFilters,
			NoArgsInstanceFactory instanceFactory) {
		super(fieldDefinitionFactory, setterFilters, getterFilters, instanceFactory);
	}

	public String getTableName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		return table == null ? StringUtils.humpNamingReplacement(clazz.getSimpleName(), "_") : table.name();
	}
}
