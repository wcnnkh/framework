package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.util.Elements;

public final class Fields extends StructureDecorator<Field, Fields> {
	public static final Function<Class<?>, Elements<AccessibleField>> DEFAULT = new AccessibleFieldFunction();

	public static Fields getFields(Class<?> sourceClass) {
		return new Fields(sourceClass).withSuperclass();
	}

	public static Fields getFields(Class<?> sourceClass, Field parentField) {
		return new Fields(sourceClass, parentField).withSuperclass();
	}

	public Fields(Class<?> sourceClass) {
		this(sourceClass, null);
	}

	public Fields(Class<?> sourceClass, Field parentField) {
		this(sourceClass, parentField, DEFAULT);
	}

	public Fields(Class<?> sourceClass, Field parent,
			Function<Class<?>, ? extends Elements<? extends AccessibleField>> processor) {
		super(sourceClass, parent, processor.andThen((e) -> e.map((o) -> new Field(parent, sourceClass, o))));
	}

	public Fields(Structure<Field> members) {
		super(members);
	}

	@Override
	protected Fields decorate(Structure<Field> structure) {
		if (structure instanceof Fields) {
			return (Fields) structure;
		}
		return new Fields(structure);
	}

	@Override
	protected Field clone(Field source) {
		return source.clone();
	}
}
