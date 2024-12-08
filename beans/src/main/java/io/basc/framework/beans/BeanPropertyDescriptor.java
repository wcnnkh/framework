package io.basc.framework.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.MergedAnnotatedElement;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Getter;
import io.basc.framework.core.execution.Getter.MergedGetter;
import io.basc.framework.core.execution.Setter;
import io.basc.framework.core.execution.Setter.MergedSetter;
import io.basc.framework.core.execution.reflect.ReflectionFieldGetter;
import io.basc.framework.core.execution.reflect.ReflectionFieldSetter;
import io.basc.framework.core.execution.reflect.ReflectionMethodGetter;
import io.basc.framework.core.execution.reflect.ReflectionMethodSetter;
import io.basc.framework.core.execution.stereotype.PropertyAccessDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.reflect.ReflectionUtils;
import lombok.NonNull;

public class BeanPropertyDescriptor implements PropertyAccessDescriptor {
	private final Class<?> beanClass;
	private volatile Field field;

	private volatile MergedGetter<?> getter;

	private final PropertyDescriptor propertyDescriptor;

	private volatile MergedSetter<?> setter;

	public BeanPropertyDescriptor(@NonNull Class<?> beanClass, @NonNull PropertyDescriptor propertyDescriptor) {
		this.beanClass = beanClass;
		this.propertyDescriptor = propertyDescriptor;
	}

	private volatile TypeDescriptor typeDescriptor;

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					MergedAnnotatedElement mergedAnnotatedElement = new MergedAnnotatedElement(
							getReadMethod().getTypeDescriptor(), getWriteMethod().getTypeDescriptor());
					ResolvableType resolvableType = ResolvableType.forClass(propertyDescriptor.getPropertyType());
					this.typeDescriptor = new TypeDescriptor(resolvableType, propertyDescriptor.getPropertyType(),
							mergedAnnotatedElement);
				}
			}
		}
		return typeDescriptor;
	}

	public Field getField() {
		if (field == null) {
			synchronized (this) {
				if (field == null) {
					field = ReflectionUtils.getDeclaredField(beanClass, propertyDescriptor.getName());
				}
			}
		}
		return field;
	}

	@Override
	public String getName() {
		return propertyDescriptor.getName();
	}

	@Override
	public MergedGetter<?> getReadMethod() {
		if (getter == null) {
			synchronized (this) {
				if (getter == null) {
					List<Getter> list = new ArrayList<>(4);
					Method method = propertyDescriptor.getReadMethod();
					if (method != null) {
						ReflectionMethodGetter methodGetter = new ReflectionMethodGetter(method);
						list.add(methodGetter);
					}

					Field field = getField();
					if (field != null) {
						ReflectionFieldGetter fieldGetter = new ReflectionFieldGetter(field);
						list.add(fieldGetter);
					}

					Elements<Getter> elements = list.isEmpty() ? Elements.empty()
							: Elements.forArray(list.toArray(new Getter[0]));
					getter = new MergedGetter<>(elements);
				}
			}
		}
		return getter;
	}

	public void refresh() {
		synchronized (this) {
			this.field = null;
			this.setter = null;
			this.getter = null;
			this.typeDescriptor = null;
		}
	}

	@Override
	public boolean isReadable() {
		return !getReadMethod().getElements().isEmpty();
	}

	@Override
	public boolean isWritable() {
		return !getWriteMethod().getElements().isEmpty();
	}

	@Override
	public MergedSetter<?> getWriteMethod() {
		if (setter == null) {
			synchronized (this) {
				if (setter == null) {
					List<Setter> list = new ArrayList<>(2);
					Method method = propertyDescriptor.getWriteMethod();
					if (method != null) {
						list.add(new ReflectionMethodSetter(method));
					}

					Field field = getField();
					if (field != null) {
						list.add(new ReflectionFieldSetter(field));
					}
					Elements<Setter> elements = list.isEmpty() ? Elements.empty()
							: Elements.forArray(list.toArray(new Setter[0]));
					this.setter = new MergedSetter<>(elements);
				}
			}
		}
		return setter;
	}
}
