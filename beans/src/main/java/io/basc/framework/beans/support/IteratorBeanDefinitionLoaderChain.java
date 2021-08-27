package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanDefinitionLoader;
import io.basc.framework.beans.BeanDefinitionLoaderChain;
import io.basc.framework.beans.ConfigurableBeanFactory;

import java.util.Iterator;

public class IteratorBeanDefinitionLoaderChain extends AbstractBeanDefinitionLoaderChain {
	private Iterator<BeanDefinitionLoader> iterator;

	public IteratorBeanDefinitionLoaderChain(Iterator<BeanDefinitionLoader> iterator) {
		this(iterator, null);
	}

	public IteratorBeanDefinitionLoaderChain(Iterator<BeanDefinitionLoader> iterator, BeanDefinitionLoaderChain chain) {
		super(chain);
		this.iterator = iterator;
	}

	@Override
	protected BeanDefinitionLoader getNext(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
