package io.basc.framework.beans;

import java.beans.BeanInfo;

import io.basc.framework.core.mapping.stereotype.MappingDescriptor;
import io.basc.framework.util.Elements;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class BeanMappingDescriptor implements MappingDescriptor<BeanFieldDescriptor> {
	private final Class<?> beanClass;
	private final BeanInfo beanInfo;
	@Setter(AccessLevel.NONE)
	private volatile Elements<BeanFieldDescriptor> elements;

	public BeanMappingDescriptor(@NonNull Class<?> beanClass, @NonNull BeanInfo beanInfo) {
		this.beanClass = beanClass;
		this.beanInfo = beanInfo;
	}

	@Override
	public String getName() {
		return beanInfo.getBeanDescriptor().getName();
	}

	@Override
	public Elements<BeanFieldDescriptor> getElements() {
		if (elements == null) {
			synchronized (this) {
				if (elements == null) {
					this.elements = Elements.forArray(beanInfo.getPropertyDescriptors())
							.map((e) -> new BeanFieldDescriptor(beanClass, e)).toList();
				}
			}
		}
		return this.elements;
	}
}
