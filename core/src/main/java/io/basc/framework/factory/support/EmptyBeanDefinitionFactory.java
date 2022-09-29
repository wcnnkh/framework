package io.basc.framework.factory.support;

import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanDefinitionFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.alias.EmptyAliasFactory;

public class EmptyBeanDefinitionFactory extends EmptyAliasFactory implements BeanDefinitionFactory {

	public static final EmptyBeanDefinitionFactory INSTANCE = new EmptyBeanDefinitionFactory();

	@Override
	public BeanDefinition getDefinition(String name) {
		return null;
	}

	@Override
	public BeanDefinition getDefinition(Class<?> clazz) {
		return null;
	}

	@Override
	public boolean containsDefinition(String beanName) {
		return false;
	}

	@Override
	public String[] getDefinitionIds() {
		return StringUtils.EMPTY_ARRAY;
	}

}
