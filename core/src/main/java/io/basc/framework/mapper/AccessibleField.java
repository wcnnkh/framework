package io.basc.framework.mapper;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.core.annotation.AnnotatedElements;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ObjectUtils;

public class AccessibleField extends AnnotatedElements implements Cloneable {
	public static final AccessibleField[] EMPTY_ARRAY = new AccessibleField[0];
	private Getter getter;
	private Setter setter;

	public AccessibleField() {
	}

	public AccessibleField(AccessibleField metadata) {
		this(metadata == null ? null : metadata.getter, metadata == null ? null : metadata.setter);
	}

	public AccessibleField(Getter getter, Setter setter) {
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected Iterator<? extends AnnotatedElement> annotationElementIterator() {
		if (isSupportGetter() && isSupportSetter()) {
			return Arrays.asList(getter, setter).iterator();
		}

		if (isSupportGetter()) {
			return Arrays.asList(getter).iterator();
		}

		if (isSupportGetter()) {
			return Arrays.asList(setter).iterator();
		}
		return Collections.emptyIterator();
	}

	@Override
	public AccessibleField clone() {
		return new AccessibleField(this);
	}

	public Getter getGetter() {
		return getter;
	}

	public Setter getSetter() {
		return setter;
	}

	public boolean isSupportGetter() {
		return getter != null;
	}

	public boolean isSupportSetter() {
		return setter != null;
	}

	public void setGetter(Getter getter) {
		this.getter = getter;
	}

	public void setSetter(Setter setter) {
		this.setter = setter;
	}

	@Override
	public int hashCode() {
		if (getter == null && setter == null) {
			return 0;
		}

		if (getter == null) {
			return setter.hashCode();
		}

		if (setter == null) {
			return getter.hashCode();
		}

		return getter.hashCode() + setter.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AccessibleField) {
			return ObjectUtils.equals(getter, ((AccessibleField) obj).getter)
					&& ObjectUtils.equals(setter, ((AccessibleField) obj).setter);
		}

		return false;
	}

	@Override
	public String toString() {
		if (isSupportGetter() && isSupportSetter()) {
			return "getter {" + getter + "} setter {" + setter + "}";
		}

		if (isSupportGetter()) {
			return "getter {" + getter + "}";
		}

		if (isSupportSetter()) {
			return "setter {" + setter + "}";
		}
		return super.toString();
	}

	public Object get(Object instance) {
		if (!isSupportGetter()) {
			return null;
		}

		if (instance == null && !Modifier.isStatic(getGetter().getModifiers())) {
			// 非静态字段的调用实例不应该为空
			return null;
		}
		return getGetter().get(instance);
	}

	public void set(Object instance, Object value) {
		if (!isSupportSetter()) {
			return;
		}

		if (instance == null && !Modifier.isStatic(getGetter().getModifiers())) {
			// 非静态字段的调用实例不应该为空
			return;
		}

		getSetter().set(instance, value);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getValues(Collection<?> instances, boolean nullable) {
		if (CollectionUtils.isEmpty(instances)) {
			return Collections.emptyList();
		}

		if (!isSupportGetter()) {
			return Collections.emptyList();
		}

		List<T> list = new ArrayList<T>(instances.size());
		for (Object entity : instances) {
			Object value = get(entity);
			if (value == null && !nullable) {
				continue;
			}

			list.add((T) value);
		}
		return list;
	}

	public <T> List<T> getValues(Collection<?> instances) {
		return getValues(instances, false);
	}
}
