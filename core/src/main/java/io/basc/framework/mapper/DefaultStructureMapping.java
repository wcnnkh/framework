package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.DefaultStructure;

final class DefaultStructureMapping<T extends Field, R extends StrctureMapping<T, R>> extends StrctureMapping<T, R> {
	private final Function<? super StrctureMapping<T, R>, ? extends R> objectMappingDecorator;

	public DefaultStructureMapping(DefaultStructure<T> members,
			Function<? super StrctureMapping<T, R>, ? extends R> objectMappingDecorator) {
		super(members);
		this.objectMappingDecorator = objectMappingDecorator;
	}

	@Override
	public Function<? super StrctureMapping<T, R>, ? extends R> getObjectMappingDecorator() {
		return objectMappingDecorator;
	}
}
