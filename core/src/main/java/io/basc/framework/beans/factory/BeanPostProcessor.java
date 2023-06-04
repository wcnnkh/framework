package io.basc.framework.beans.factory;

import io.basc.framework.beans.BeansException;

public interface BeanPostProcessor {
	<T> void processPostBean(String beanName, T bean) throws BeansException;
}
