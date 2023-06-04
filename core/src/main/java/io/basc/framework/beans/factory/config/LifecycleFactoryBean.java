package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.FactoryBean;

public interface LifecycleFactoryBean<T> extends FactoryBean<T> {

	void destroy(Object bean) throws BeansException;

	void init(Object bean) throws BeansException;

	void dependence(Object bean) throws BeansException;
}
