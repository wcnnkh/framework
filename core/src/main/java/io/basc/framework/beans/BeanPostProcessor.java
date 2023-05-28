package io.basc.framework.beans;

public interface BeanPostProcessor {
	<T> void processPostBean(String beanName, T bean) throws BeansException;
}
