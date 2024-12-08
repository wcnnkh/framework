package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.function.Function;

import io.basc.framework.core.execution.stereotype.PropertyAccessTemplate;
import io.basc.framework.util.Elements;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class BeaninfoTemplate<T extends BeanPropertyDescriptor> implements PropertyAccessTemplate<T> {
	private final Class<?> beanClass;
	private final BeanInfo beanInfo;
	private final Function<? super PropertyDescriptor, ? extends T> function;
	@Setter(AccessLevel.NONE)
	private volatile Elements<T> elements;

	public BeaninfoTemplate(@NonNull Class<?> beanClass, @NonNull BeanInfo beanInfo,
			@NonNull Function<? super PropertyDescriptor, ? extends T> function) {
		this.beanClass = beanClass;
		this.beanInfo = beanInfo;
		this.function = function;
	}

	@Override
	public String getName() {
		return beanInfo.getBeanDescriptor().getName();
	}

	@Override
	public Elements<T> getElements() {
		if (elements == null) {
			synchronized (this) {
				if (elements == null) {
					this.elements = Elements.forArray(beanInfo.getPropertyDescriptors()).map(function);
				}
			}
		}
		return this.elements;
	}
}
