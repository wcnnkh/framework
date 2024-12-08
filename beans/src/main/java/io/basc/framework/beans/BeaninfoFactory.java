package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import lombok.NonNull;

public interface BeaninfoFactory {
	BeanInfo getBeaninfo(@NonNull Class<?> beanClass) throws IntrospectionException;
}
