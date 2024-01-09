package io.basc.framework.beans;

import java.beans.BeanInfo;

import io.basc.framework.mapper.Mapping;
import io.basc.framework.util.element.Elements;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

@Data
public final class BeanMapping implements Mapping<BeanItem> {
	private final Class<?> beanClass;
	private final BeanInfo beanInfo;
	@NonNull
	private Elements<String> aliasNames = Elements.empty();
	@Setter(AccessLevel.NONE)
	private volatile Elements<BeanItem> elements;

	public BeanMapping(Class<?> beanClass, BeanInfo beanInfo) {
		this.beanClass = beanClass;
		this.beanInfo = beanInfo;
	}

	@Override
	public String getName() {
		return beanInfo.getBeanDescriptor().getName();
	}

	private Elements<BeanItem> parseElements() {
		return Elements.forArray(beanInfo.getPropertyDescriptors()).map((e) -> new BeanItem(beanClass, e)).toList();
	}

	@Override
	public Elements<BeanItem> getElements() {
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
