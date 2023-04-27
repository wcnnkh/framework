package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.DefaultStructure;
import io.basc.framework.util.Elements;

public final class Fields extends ObjectMapping<DefaultField, Fields> {
	private final Function<? super ObjectMapping<DefaultField, Fields>, ? extends Fields> objectMappingDecorator = (
			mapping) -> new Fields(mapping);

	public Fields(Class<?> source, Function<? super Class<?>, ? extends Elements<DefaultField>> processor) {
		super(source, processor);
	}

	private Fields(DefaultStructure<DefaultField> members) {
		super(members);
	}

	@Override
	public final Function<? super ObjectMapping<DefaultField, Fields>, ? extends Fields> getObjectMappingDecorator() {
		return objectMappingDecorator;
	}

	public static Fields getFields(Class<?> sourceClass) {
		return new Fields(sourceClass, FieldsGenerator.DEFAULT);
	}
}
