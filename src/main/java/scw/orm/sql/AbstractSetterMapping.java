package scw.orm.sql;

import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.SetterMapping;

public abstract class AbstractSetterMapping implements SetterMapping {
	public void setter(MappingContext context, Object bean, MappingOperations ormOperations) throws Exception {
		if (SqlORMUtils.ignoreField(context.getFieldDefinition())) {
			return;
		}

		if (SqlORMUtils.isDataBaseField(context.getFieldDefinition())) {
			ormOperations.setter(context, bean, getValue(context));
		} else {
			ormOperations.create(context, context.getFieldDefinition().getField().getType(), this);
		}
	}

	protected abstract Object getValue(MappingContext context);
}