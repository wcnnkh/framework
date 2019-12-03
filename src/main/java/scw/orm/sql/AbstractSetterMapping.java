package scw.orm.sql;

import scw.orm.MappingContext;
import scw.orm.SetterMapping;

public abstract class AbstractSetterMapping implements SetterMapping<SqlMapper> {

	public void setter(MappingContext context, Object bean, SqlMapper mappingOperations) throws Exception {
		mappingOperations.setter(context, bean, getValue(context, mappingOperations));
	}

	protected abstract Object getValue(MappingContext context, SqlMapper mappingOperations);
}