package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.DefaultStructure;

final class DefaultObjectMapping<T extends DefaultField, R extends ObjectMapping<T, R>> extends ObjectMapping<T, R> {
	private final Function<? super ObjectMapping<T, R>, ? extends R> objectMappingDecorator;

	public DefaultObjectMapping(DefaultStructure<T> members,
			Function<? super ObjectMapping<T, R>, ? extends R> objectMappingDecorator) {
		super(members);
		this.objectMappingDecorator = objectMappingDecorator;
	}

	@Override
	public Function<? super ObjectMapping<T, R>, ? extends R> getObjectMappingDecorator() {
		return objectMappingDecorator;
	}
}
