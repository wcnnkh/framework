package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.stereotype.StereotypeTemplateProvider;
import lombok.NonNull;

public interface BeanInfoProvider
		extends BeanInfoFactory, StereotypeTemplateProvider<BeanPropertyDescriptor, BeanMapping> {

	@Override
	default BeanMapping getStereotypeMapping(@NonNull TypeDescriptor requiredType) {
		Class<?> beanClass = requiredType.getType();
		BeanInfo beanInfo;
		try {
			beanInfo = getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass + "]", e);
		}
		return beanInfo == null ? null : new BeanMapping(beanClass, beanInfo);
	}
}
