package io.basc.framework.mapper;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.env.Sys;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.value.EmptyValue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
	public List<Field> getParents() {
		Iterator<Field> iterator = new ParentFieldIterator(this);
		return CollectionUtils.reversal(Collections.list(CollectionUtils.toEnumeration(iterator)));
	}

	public boolean hasParent() {
		return parentField != null;
	}

	public Object get(Object instance) {
		if (parentField == null) {
			return getGetter().get(instance);
		}

		Object parentValue = instance;
		for (Field parentField : getParents()) {
			if (!parentField.isSupportGetter()) {
				return EmptyValue.INSTANCE.getAsObject(getGetter().getType());
			}

			boolean isStatic = Modifier.isStatic(parentField.getGetter().getModifiers());
			if (isStatic) {
				// 如果是静态方法
				parentValue = null;
			} else {
				parentValue = parentField.getGetter().get(parentValue);
				// 如果不是静态的，但获取到的是空就不用再向下获取了
				if (parentValue == null) {
					return EmptyValue.INSTANCE.getAsObject(getGetter().getType());
				}
			}
		}
		return getGetter().get(parentValue);
	}

	public void set(Object instance, Object value, ConversionService conversionService) {
		set(instance, value, Sys.env, conversionService);
	}

	public void set(Object instance, Object value, NoArgsInstanceFactory instanceFactory,
			ConversionService conversionService) {
		if (parentField == null) {
			getSetter().set(instance, value, conversionService);
			return;
		}

		Object parentValue = instance;
		for (Field parentField : getParents()) {
			boolean isStatic = Modifier.isStatic(parentField.getGetter().getModifiers());
			if (isStatic) {
				// 如果是静态方法
				parentValue = null;
			} else {
				Object target = parentField.getGetter().get(parentValue);
				if (target == null) {
					if (value == null) {
						return;
					}

					if (!instanceFactory.isInstance(parentField.getSetter().getType())) {
						throw new NotSupportedException(parentField.toString());
					}

					target = instanceFactory.getInstance(parentField.getSetter().getType());
					parentField.getSetter().set(parentValue, target);
				}
				parentValue = target;
			}
		}
		getSetter().set(parentValue, value, conversionService);
	}
}
