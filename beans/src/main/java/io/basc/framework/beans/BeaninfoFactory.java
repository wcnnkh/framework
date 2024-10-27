package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import io.basc.framework.lang.Nullable;

public interface BeaninfoFactory {
	@Nullable
	BeanInfo getBeaninfo(Class<?> beanClass) throws IntrospectionException;
}
