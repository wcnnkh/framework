package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.DefaultStructure;
import io.basc.framework.util.Elements;

public final class DefaultObjectMapping extends ObjectMapping<DefaultField, DefaultObjectMapping> {
	private final Function<? super ObjectMapping<DefaultField, DefaultObjectMapping>, ? extends DefaultObjectMapping> objectMappingDecorator = (
			mapping) -> new DefaultObjectMapping(mapping);

	public DefaultObjectMapping(Class<?> source,
			Function<? super Class<?>, ? extends Elements<DefaultField>> processor) {
		super(source, processor);
	}

	private DefaultObjectMapping(DefaultStructure<DefaultField> members) {
		super(members);
	}

	@Override
	public final Function<? super ObjectMapping<DefaultField, DefaultObjectMapping>, ? extends DefaultObjectMapping> getObjectMappingDecorator() {
		return objectMappingDecorator;
	}

}
