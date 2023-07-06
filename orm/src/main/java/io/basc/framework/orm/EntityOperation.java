package io.basc.framework.orm;

import java.util.List;
import java.util.OptionalLong;

import io.basc.framework.data.repository.OperationSymbol;

@FunctionalInterface
public interface EntityOperation {
	List<OptionalLong> execute(EntityMapper entityMapper, OperationSymbol operationSymbol, Class<?> entityClass,
			EntityMapping<?> entityMapping, List<? extends Object> entitys) throws OrmException;
}
