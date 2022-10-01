package io.basc.framework.factory.support;

import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;

public class BeanDefinitionLoaderChain {
	private final Iterator<BeanDefinitionLoader> iterator;
	private final BeanDefinitionLoaderChain nextChain;

	public BeanDefinitionLoaderChain(Iterator<BeanDefinitionLoader> iterator) {
		this(iterator, null);
	}

	public BeanDefinitionLoaderChain(Iterator<BeanDefinitionLoader> iterator,
			@Nullable BeanDefinitionLoaderChain nextChain) {
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	public BeanDefinition load(BeanFactory beanFactory, String name) throws FactoryException {
		if (iterator.hasNext()) {
			return iterator.next().load(beanFactory, name, this);
		}

		if (nextChain == null) {
			Class<?> clazz = ClassUtils.getClass(name, beanFactory.getClassLoader());
			if (clazz == null) {
				return null;
			}

			ServiceLoaderBeanDefinition serviceLoaderBeanDefinition = new ServiceLoaderBeanDefinition(beanFactory,
					clazz);
			if (serviceLoaderBeanDefinition.isInstance()) {
				return serviceLoaderBeanDefinition;
			}

			return new FactoryBeanDefinition(beanFactory, TypeDescriptor.valueOf(clazz));
		}
		return nextChain.load(beanFactory, name);
	}

}