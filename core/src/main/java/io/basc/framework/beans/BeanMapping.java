package io.basc.framework.beans;

import java.beans.BeanInfo;

import io.basc.framework.mapper.entity.Mapping;
import io.basc.framework.util.element.Elements;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

@Data
public final class BeanMapping implements Mapping<BeanFieldDescriptor> {
	private final Class<?> beanClass;
	private final BeanInfo beanInfo;
	@NonNull
	private Elements<String> aliasNames = Elements.empty();
	@Setter(AccessLevel.NONE)
	private volatile Elements<BeanFieldDescriptor> elements;

	public BeanMapping(Class<?> beanClass, BeanInfo beanInfo) {
		this.beanClass = beanClass;
		this.beanInfo = beanInfo;
	}

	@Override
	public String getName() {
		return beanInfo.getBeanDescriptor().getName();
	}

	private Elements<BeanFieldDescriptor> parseElements() {
		return Elements.forArray(beanInfo.getPropertyDescriptors()).map((e) -> new BeanFieldDescriptor(beanClass, e)).toList();
	}

	@Override
	public Elements<BeanFieldDescriptor> getElements() {
		if (elements == null) {
			synchronized (this) {
				if (elements == null) {
					this.elements = parseElements();
				}
			}
		}
		return this.elements;
	}
}
