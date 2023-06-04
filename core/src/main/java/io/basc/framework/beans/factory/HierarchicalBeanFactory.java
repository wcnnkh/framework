package io.basc.framework.beans.factory;

import io.basc.framework.lang.Nullable;

public interface HierarchicalBeanFactory extends BeanFactory {
	@Nullable
	BeanFactory getParentBeanFactory();

	boolean containsLocalBean(String name);

	default Scope getScop(String beanName) throws NoSuchBeanDefinitionException {
		BeanFactory beanFactory = this;
		while (beanFactory != null) {
			if (beanFactory.containsBean(beanName)) {
				return beanFactory.getScope();
			}

			if (beanFactory instanceof HierarchicalBeanFactory) {
				beanFactory = ((HierarchicalBeanFactory) beanFactory).getParentBeanFactory();
			} else {
				break;
			}
		}
		throw new NoSuchBeanDefinitionException(beanName);
	}
}
