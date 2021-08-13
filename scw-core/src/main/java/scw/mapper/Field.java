package scw.mapper;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import scw.core.utils.CollectionUtils;
import scw.util.stream.StreamProcessorSupport;

public final class Field extends FieldMetadata {
	private final Field parentField;

	public Field(Field parentField, Class<?> declaringClass, String name, java.lang.reflect.Field field, Method getter,
			Method setter) {
		this(parentField,
				(field == null && getter == null) ? null : new DefaultGetter(declaringClass, name, field, getter),
				(field == null && setter == null) ? null : new DefaultSetter(declaringClass, name, field, setter));
	}

	public Field(Field parentField, Getter getter, Setter setter) {
		super(getter, setter);
		this.parentField = parentField;
	}

	public Field(Field parentField, FieldMetadata metadata) {
		super(metadata);
		this.parentField = parentField;
	}

	public Field getParentField() {
		return parentField;
	}

	/**
	 * 获取所有的父级
	 * 
	 * @return
	 */
	public Stream<Field> parents() {
		Iterator<Field> iterator = new ParentFieldIterator(this);
		return StreamProcessorSupport.stream(iterator);
	}

	public Object get(Object instance) {
		if (parentField == null) {
			return getGetter().get(instance);
		}

		Object parentValue = instance;
		for (Field parentField : CollectionUtils.reversal(parents().collect(Collectors.toList()))) {
			parentValue = parentField.getGetter().get(parentValue);
		}
		return getGetter().get(parentValue);
	}
}
