package scw.orm.sql;

import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.ValueMapping;

public abstract class AbstractValueMapping implements ValueMapping {
	public void iterator(MappingContext context, Object bean, MappingOperations ormOperations) throws Exception {
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