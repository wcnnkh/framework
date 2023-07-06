package io.basc.framework.orm.filter;

import java.util.List;
import java.util.OptionalLong;

import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityOperation;
import io.basc.framework.orm.OrmException;

public interface EntityOperationFilter {
	List<OptionalLong> execute(EntityMapper entityMapper, OperationSymbol operationSymbol, Class<?> entityClass,
			EntityMapping<?> entityMapping, List<? extends Object> entitys, EntityOperation groundEntityOperation)
			throws OrmException;
}
