package scw.beans.support;

import java.util.Iterator;

import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.ConfigurableBeanFactory;

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
