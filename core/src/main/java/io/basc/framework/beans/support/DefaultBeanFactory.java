package io.basc.framework.beans.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.FactoryBean;
import io.basc.framework.beans.Scope;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

public class DefaultBeanFactory extends DefaultBeanDefinitionRegistry implements BeanFactory {
	private final Map<String, FactoryBean<Object>> factoryBeans = new ConcurrentHashMap<>();
	private final BeanFactory parent;
	private final Scope scope;
	private volatile boolean initialized;

	public DefaultBeanFactory(Scope scope, @Nullable BeanFactory parent) {
		this.scope = scope;
		this.parent = parent;
	}

	@Override
	public BeanFactory getParent() {
		return parent;
	}

	@Override
	public Elements<? extends FactoryBean<Object>> getBeans() {
		return Elements.of(factoryBeans.values());
	}

	@Override
	public Scope getScope() {
		return scope;
	}

	@Override
	public boolean containsBean(String beanName) {
		return factoryBeans.containsKey(beanName);
	}

	@Override
	public <T> FactoryBean<T> getBean(Class<? extends T> requiredType) throws BeansException {
		
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FactoryBean<Object> getBean(ResolvableType requiredType) throws BeansException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

}
