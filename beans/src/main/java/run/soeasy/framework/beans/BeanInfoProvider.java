package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.transform.mapping.MappingTemplateProvider;

public interface BeanInfoProvider
		extends BeanInfoFactory, MappingTemplateProvider<BeanPropertyDescriptor, BeanMapping> {
	
	@Override
	default BeanMapping getMappingDescriptor(@NonNull TypeDescriptor requiredType) {
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
