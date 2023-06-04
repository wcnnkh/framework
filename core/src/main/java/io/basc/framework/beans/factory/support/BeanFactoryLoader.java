package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.HierarchicalBeanFactory;
import io.basc.framework.beans.factory.NoSuchBeanDefinitionException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.core.ResolvableType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BeanFactoryLoader implements HierarchicalBeanFactory {
	private final BeanFactory beanFactory;
	private final BeanFactory parentBeanFactory;

	@Override
	public Scope getScope() {
		return beanFactory.getScope();
	}

	@Override
	public boolean containsBean(String name) {
		return beanFactory.containsBean(name) || parentBeanFactory.containsBean(name);
	}

	@Override
	public boolean isFactoryBean(String name) {
		return beanFactory.isFactoryBean(name) || parentBeanFactory.isFactoryBean(name);
	}

	@Override
	public Object getBean(String name) throws BeansException {
		if (beanFactory.containsBean(name)) {
			return beanFactory.getBean(name);
		}

		if (parentBeanFactory.containsBean(name)) {
			return parentBeanFactory.containsBean(name);
		}
		throw new NoSuchBeanDefinitionException(name);
	}

	@Override
	public FactoryBean<? extends Object> getFactoryBean(String beanName) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getBean(Class<? extends T> requiredType) throws BeansException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getBean(ResolvableType requiredType) throws BeansException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAlias(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAlias(String name, String alias) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getAliases(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BeanFactory getParentBeanFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsLocalBean(String name) {
		// TODO Auto-generated method stub
		return false;
	}

}
