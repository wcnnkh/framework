package io.basc.framework.mapper;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.env.Sys;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.EmptyValue;

public class Field extends AccessibleField implements Member, ParentDiscover<Field> {
	protected Field parent;
	protected Class<?> declaringClass;
	protected Collection<String> aliasNames;
	protected Integer modifiers;
	protected Boolean synthetic;
	protected String name;

	public Field() {
	}

	public Field(Field parent, Class<?> declaringClass, String name, java.lang.reflect.Field field, Method getter,
			Method setter) {
		this(parent, declaringClass,
				(field == null && getter == null) ? null : new DefaultGetter(declaringClass, name, field, getter),
				(field == null && setter == null) ? null : new DefaultSetter(declaringClass, name, field, setter));
	}

	public Field(Field parent, Class<?> declaringClass, Getter getter, Setter setter) {
		super(getter, setter);
		this.declaringClass = declaringClass;
		this.parent = parent;
	}

	public Field(Field parent, Class<?> declaringClass, AccessibleField metadata) {
		super(metadata);
		this.parent = parent;
		this.declaringClass = declaringClass;
	}

	public Field(Field field) {
		super(field);
		if (field != null) {
			this.parent = field.parent;
			this.declaringClass = field.declaringClass;
			this.aliasNames = field.aliasNames;
		}
	}

	public Field clone() {
		return new Field(this);
	}

	public Field getParent() {
		return parent;
	}

	public void setParent(Field parent) {
		this.parent = parent;
	}

	public Object get(Object instance) {
		if (parent == null) {
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
		if (parent == null) {
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

	public <V, E extends Throwable> V getValueByNames(Processor<String, V, E> processor) throws E {
		if (isSupportSetter()) {
			V value = processor.process(getSetter().getName());
			if (value != null) {
				return value;
			}
		}

		if (isSupportGetter()) {
			V value = processor.process(getGetter().getName());
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return this.declaringClass;
	}

	@Override
	public String getName() {
		if (StringUtils.isNotEmpty(name)) {
			return name;
		}

		if (isSupportGetter()) {
			return getGetter().getName();
		}

		if (isSupportSetter()) {
			return getSetter().getName();
		}
		return null;
	}

	public Collection<String> getAliasNames() {
		if (aliasNames != null) {
			return Collections.unmodifiableCollection(aliasNames);
		}

		if (isSupportGetter() && isSupportSetter()) {
			return Arrays.asList(getSetter().getName());
		}
		return Collections.emptyList();
	}

	public void setAliasNames(Collection<String> aliasNames) {
		this.aliasNames = aliasNames;
	}

	@Override
	public int getModifiers() {
		if (modifiers != null) {
			return modifiers;
		}

		if (isSupportGetter() && isSupportSetter()) {
			return getGetter().getModifiers() | getSetter().getModifiers();
		}

		if (isSupportGetter()) {
			return getGetter().getModifiers();
		}

		if (isSupportSetter()) {
			return getSetter().getModifiers();
		}
		return 0;
	}

	@Override
	public boolean isSynthetic() {
		if (synthetic != null) {
			return synthetic;
		}

		if (isSupportGetter() && isSupportSetter()) {
			return getGetter().isSynthetic() || getSetter().isSynthetic();
		}

		if (isSupportGetter()) {
			return getGetter().isSynthetic();
		}

		if (isSupportSetter()) {
			return getSetter().isSynthetic();
		}

		return false;
	}

	public void setModifiers(Integer modifiers) {
		this.modifiers = modifiers;
	}

	public void setSynthetic(Boolean synthetic) {
		this.synthetic = synthetic;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Field rename(String name) {
		if (StringUtils.isEmpty(name)) {
			return this;
		}

		Field field = clone();
		field.setName(name);
		return field;
	}
}
