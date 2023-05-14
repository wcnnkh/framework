package io.basc.framework.orm.filter;

import java.util.OptionalLong;

import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityOperation;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public interface EntityOperationFilter {
	Elements<OptionalLong> execute(OperationSymbol operationSymbol, EntityMapping<? extends Property> entityMapping,
			Elements<? extends Value> entitys, EntityOperation groundEntityOperation) throws OrmException;
}
