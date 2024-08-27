package io.basc.framework.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Getter;
import io.basc.framework.execution.MergedGetter;
import io.basc.framework.execution.MergedSetter;
import io.basc.framework.execution.Setter;
import io.basc.framework.execution.reflect.ReflectionFieldGetter;
import io.basc.framework.execution.reflect.ReflectionFieldSetter;
import io.basc.framework.execution.reflect.ReflectionMethodGetter;
import io.basc.framework.execution.reflect.ReflectionMethodSetter;
import io.basc.framework.mapper.stereotype.FieldDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public final class BeanFieldDescriptor implements FieldDescriptor {
	private final Class<?> beanClass;
	private volatile Field field;

	private volatile MergedGetter getter;

	private final PropertyDescriptor propertyDescriptor;

	private volatile MergedSetter setter;

	public BeanFieldDescriptor(Class<?> beanClass, PropertyDescriptor propertyDescriptor) {
		Assert.requiredArgument(beanClass != null, "beanClass");
		Assert.requiredArgument(propertyDescriptor != null, "propertyDescriptor");
		this.beanClass = beanClass;
		this.propertyDescriptor = propertyDescriptor;
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
	public MergedGetter getter() {
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
					getter = new MergedGetter(elements);
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
		}
	}

	@Override
	public boolean isSupportGetter() {
		return !getter().getElements().isEmpty();
	}

	@Override
	public boolean isSupportSetter() {
		return !setter().getElements().isEmpty();
	}

	@Override
	public MergedSetter setter() {
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
					this.setter = new MergedSetter(elements);
				}
			}
		}
		return setter;
	}
}
