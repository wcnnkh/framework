package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.Members;

final class ObjectMappingWrapper<T extends Member, R extends ObjectMapping<T, R>> extends ObjectMapping<T, R> {
	private final Function<? super ObjectMapping<T, R>, ? extends R> objectMappingDecorator;

	public ObjectMappingWrapper(Members<T> members,
			Function<? super ObjectMapping<T, R>, ? extends R> objectMappingDecorator) {
		super(members);
		this.objectMappingDecorator = objectMappingDecorator;
	}

	@Override
	public Function<? super ObjectMapping<T, R>, ? extends R> getObjectMappingDecorator() {
		return objectMappingDecorator;
	}
}
