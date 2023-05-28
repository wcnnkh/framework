package io.basc.framework.beans;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.alias.AliasFactory;

public interface BeanFactory extends AliasFactory, ParentDiscover<BeanFactory> {
	Elements<? extends FactoryBean<Object>> getBeans();

	Scope getScope();

	boolean containsBean(String beanName);

	<T> FactoryBean<T> getBean(Class<? extends T> requiredType) throws BeansException;

	FactoryBean<Object> getBean(ResolvableType requiredType) throws BeansException;

	@SuppressWarnings("unchecked")
	default <T> Elements<? extends FactoryBean<T>> getBeans(Class<? extends T> requiredType) {
		return getBeans().filter(
				(factoryBean) -> factoryBean.getTypeDescriptor().getResolvableType().isAssignableFrom(requiredType))
				.map((factoryBean) -> (FactoryBean<T>) factoryBean);
	}

	default Elements<? extends FactoryBean<Object>> getBeans(ResolvableType requiredType) {
		return getBeans().filter(
				(factoryBean) -> factoryBean.getTypeDescriptor().getResolvableType().isAssignableFrom(requiredType));
	}

	/**
	 * 是否已初始化
	 * 
	 * @return
	 */
	boolean isInitialized();
}
