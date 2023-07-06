package io.basc.framework.orm.filter;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityOperation;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.Assert;

public class EntityOperationFilterChain implements EntityOperation {
	private final Iterator<? extends EntityOperationFilter> iterator;
	private final EntityOperation nextChain;

	public EntityOperationFilterChain(Iterator<? extends EntityOperationFilter> iterator) {
		this(iterator, null);
	}

	public EntityOperationFilterChain(Iterator<? extends EntityOperationFilter> iterator,
			@Nullable EntityOperation nextChain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	@Override
	public List<OptionalLong> execute(EntityMapper entityMapper, OperationSymbol operationSymbol, Class<?> entityClass,
			EntityMapping<?> entityMapping, List<? extends Object> entitys) throws OrmException {
		if (iterator.hasNext()) {
			return iterator.next().execute(entityMapper, operationSymbol, entityClass, entityMapping, entitys, this);
		}

		return nextChain == null ? entitys.stream().map((e) -> OptionalLong.empty()).collect(Collectors.toList())
				: nextChain.execute(entityMapper, operationSymbol, entityClass, entityMapping, entitys);
	}

}
