package io.basc.framework.mapper;

import java.util.function.Function;
import java.util.stream.Stream;

public final class Fields extends StructureDecorator<Field, Fields> {
	public static final Function<Class<?>, Stream<AccessibleField>> DEFAULT = new AccessibleFieldFunction();

	public static Fields getFields(Class<?> sourceClass) {
		return new Fields(sourceClass).withSuperclass();
	}

	public static Fields getFields(Class<?> sourceClass, Field parentField) {
		return new Fields(sourceClass, parentField).withSuperclass();
	}

	protected Field parent;

	public Fields(Class<?> sourceClass) {
		this(sourceClass, null);
	}

	public Fields(Class<?> sourceClass, Field parentField) {
		this(sourceClass, parentField, DEFAULT);
	}

	public Fields(Class<?> sourceClass, Field parent,
			Function<Class<?>, ? extends Stream<? extends AccessibleField>> processor) {
		super(sourceClass, processor.andThen((e) -> e.map((o) -> new Field(parent, sourceClass, o))));
		this.parent = parent;
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
