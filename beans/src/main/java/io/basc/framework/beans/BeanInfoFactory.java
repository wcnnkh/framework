package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import lombok.NonNull;

public interface BeanInfoFactory {
	BeanInfo getBeanInfo(@NonNull Class<?> beanClass) throws IntrospectionException;
}
