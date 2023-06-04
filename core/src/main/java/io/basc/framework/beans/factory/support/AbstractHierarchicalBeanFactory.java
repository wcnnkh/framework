package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.HierarchicalBeanFactory;

public abstract class AbstractHierarchicalBeanFactory extends AbstractListableBeanFactory {
	private BeanFactory parentBeanFactory;

	public BeanFactory getParentBeanFactory() {
		return parentBeanFactory;
	}

	public void setParentBeanFactory(BeanFactory parentBeanFactory) {
		if (parentBeanFactory == this) {

		}

		BeanFactory parent = parentBeanFactory;
		while (parent != null) {
			if (parent == this) {
				throw new BeansException("There is a circular dependency between Beanfactory " + this
						+ " and BeanFactory " + parentBeanFactory);
			}

			if (parent instanceof HierarchicalBeanFactory) {
				parent = ((HierarchicalBeanFactory) parent).getParentBeanFactory();
			}
		}
		this.parentBeanFactory = parentBeanFactory;
	}

	@Override
	public boolean containsLocalBean(String name) {
		return super.containsBean(name);
	}
}
