package scw.orm.sql;

import scw.orm.MappingContext;
import scw.orm.SetterMapping;

public abstract class AbstractSetterMapping implements SetterMapping<SqlMapper> {

	public void setter(MappingContext context, Object bean, SqlMapper mappingOperations) throws Exception {
		if (mappingOperations.isIgnore(context)) {
			return;
		}

		if (mappingOperations.isDataBaseMappingContext(context)) {
			mappingOperations.setter(context, bean, getValue(context, mappingOperations));
		} else {
			mappingOperations.create(context, context.getColumn().getField().getType(), this);
		}
	}

	protected abstract Object getValue(MappingContext context, SqlMapper mappingOperations);
}