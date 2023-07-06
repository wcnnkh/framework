package io.basc.framework.orm.filter;

import java.util.List;
import java.util.OptionalLong;

import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityOperation;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.Assert;

public class FilterableEntityOperation implements EntityOperation {
	private final Iterable<? extends EntityOperationFilter> filters;
	private final EntityOperation groundEntityOperation;

	public FilterableEntityOperation(Iterable<? extends EntityOperationFilter> filters,
			@Nullable EntityOperation groundEntityOperation) {
		Assert.requiredArgument(filters != null, "filters");
		this.filters = filters;
		this.groundEntityOperation = groundEntityOperation;
	}

	@Override
	public List<OptionalLong> execute(EntityMapper entityMapper, OperationSymbol operationSymbol, Class<?> entityClass,
			EntityMapping<?> entityMapping, List<? extends Object> entitys) throws OrmException {
		EntityOperation entityOperation = new EntityOperationFilterChain(this.filters.iterator(),
				this.groundEntityOperation);
		return entityOperation.execute(entityMapper, operationSymbol, entityClass, entityMapping, entitys);
	}

}
