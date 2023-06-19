package io.basc.framework.beans.factory.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import io.basc.framework.beans.factory.NoSuchBeanDefinitionException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionOverrideException;
import io.basc.framework.util.Elements;

public abstract class AbstractListableBeanFactory extends AbstractBeanFactory {
	private final Map<String, BeanDefinition> definitionMap = new ConcurrentHashMap<String, BeanDefinition>();

	protected BeanDefinition getBeanDefinitionOfCache(String beanName) {
		BeanDefinition beanDefinition = definitionMap.get(beanName);
		if (beanDefinition == null) {
			for (String alias : getAliases(beanName)) {
				beanDefinition = definitionMap.get(alias);
				if (beanDefinition != null) {
					break;
				}
			}
		}
		return beanDefinition;
	}

	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
		Lock writeLock = getReadWriteLock().writeLock();
		writeLock.lock();
		try {
			BeanDefinition cache = getBeanDefinitionOfCache(beanName);
			if (cache != null) {
				throw new BeanDefinitionOverrideException(beanName, beanDefinition, cache);
			}

			definitionMap.put(beanName, beanDefinition);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void removeBeanDefinition(String beanName) {
		BeanDefinition old = definitionMap.remove(beanName);
		if (old == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		if (definitionMap.containsKey(beanName)) {
			return true;
		}

		for (String alias : getAliases(beanName)) {
			if (definitionMap.containsKey(alias)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Elements<String> getBeanDefinitionNames() {
		return Elements.of(definitionMap.keySet());
	}
}
