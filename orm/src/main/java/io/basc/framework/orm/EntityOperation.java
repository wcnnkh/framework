package io.basc.framework.orm;

import java.util.OptionalLong;

import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

@FunctionalInterface
public interface EntityOperation {
	Elements<OptionalLong> execute(OperationSymbol operationSymbol, EntityMapping<? extends Property> entityMapping,
			Elements<? extends Value> entitys) throws OrmException;
}
