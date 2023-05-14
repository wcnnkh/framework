package io.basc.framework.orm.filter;

import java.util.Iterator;
import java.util.OptionalLong;

import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityOperation;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

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
	public Elements<OptionalLong> execute(OperationSymbol operationSymbol,
			EntityMapping<? extends Property> entityMapping, Elements<? extends Value> entitys) throws OrmException {
		if (iterator.hasNext()) {
			return iterator.next().execute(operationSymbol, entityMapping, entitys, this);
		}

		return nextChain == null ? entitys.map((e) -> OptionalLong.empty())
				: nextChain.execute(operationSymbol, entityMapping, entitys);
	}

}
