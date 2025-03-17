package run.soeasy.framework.beans.factory.config;

import run.soeasy.framework.beans.BeansException;
import run.soeasy.framework.beans.factory.FactoryBean;

public interface LifecycleFactoryBean<T> extends FactoryBean<T> {

	void init(Object bean) throws BeansException;

	void destroy(Object bean) throws BeansException;
}
