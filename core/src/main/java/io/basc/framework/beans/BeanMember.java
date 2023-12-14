package io.basc.framework.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.mapper.Member;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Setter;
import io.basc.framework.mapper.reflect.FieldGetter;
import io.basc.framework.mapper.reflect.FieldSetter;
import io.basc.framework.mapper.reflect.MethodGetter;
import io.basc.framework.mapper.reflect.MethodSetter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

@lombok.Getter
public final class BeanMember implements Member {
	private final Class<?> beanClass;
	private final PropertyDescriptor propertyDescriptor;
	private Field field;
	private Elements<? extends Getter> getters;
	private Elements<? extends Setter> setters;

	public BeanMember(Class<?> beanClass, PropertyDescriptor propertyDescriptor) {
		Assert.requiredArgument(beanClass != null, "beanClass");
		Assert.requiredArgument(propertyDescriptor != null, "propertyDescriptor");
		this.beanClass = beanClass;
		this.propertyDescriptor = propertyDescriptor;
	}

	@Override
	public String getName() {
		return propertyDescriptor.getName();
	}

	public void refresh() {
		this.field = ReflectionUtils.getDeclaredField(beanClass, propertyDescriptor.getName());
		this.getters = parseGetters();
		this.setters = parseSetters();
	}

	private Elements<? extends Getter> parseGetters() {
		List<Getter> list = new ArrayList<>(2);
		Method method = propertyDescriptor.getReadMethod();
		if (method != null) {
			MethodGetter methodGetter = new MethodGetter(method);
			list.add(methodGetter);
		}

		Field field = getField();
		if (field != null) {
			FieldGetter fieldGetter = new FieldGetter(field);
			list.add(fieldGetter);
		}

		return list.isEmpty() ? Elements.empty() : Elements.forArray(list.toArray(new Getter[0]));
	}

	private Elements<? extends Setter> parseSetters() {
		List<Setter> list = new ArrayList<>(2);
		Method method = propertyDescriptor.getWriteMethod();
		if (method != null) {
			list.add(new MethodSetter(method));
		}

		Field field = getField();
		if (field != null) {
			list.add(new FieldSetter(field));
		}
		return list.isEmpty() ? Elements.empty() : Elements.forArray(list.toArray(new Setter[0]));
	}
}
