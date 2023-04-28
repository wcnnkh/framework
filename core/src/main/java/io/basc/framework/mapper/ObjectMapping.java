package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.DefaultStructure;
import io.basc.framework.util.Elements;

public final class ObjectMapping extends StrctureMapping<DefaultField, ObjectMapping> {
	private static final FieldsGenerator FIELDS_GENERATOR = new FieldsGenerator();

	private final Function<? super StrctureMapping<DefaultField, ObjectMapping>, ? extends ObjectMapping> objectMappingDecorator = (
			mapping) -> new ObjectMapping(mapping);

	public ObjectMapping(Class<?> source, Function<? super Class<?>, ? extends Elements<DefaultField>> processor) {
		super(source, processor);
	}

	private ObjectMapping(DefaultStructure<DefaultField> members) {
		super(members);
	}

	@Override
	public final Function<? super StrctureMapping<DefaultField, ObjectMapping>, ? extends ObjectMapping> getObjectMappingDecorator() {
		return objectMappingDecorator;
	}

	public static ObjectMapping getMapping(Class<?> sourceClass) {
		return new ObjectMapping(sourceClass, FIELDS_GENERATOR);
	}

}
