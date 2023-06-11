package io.basc.framework.beans.factory.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import io.basc.framework.beans.factory.NoSuchBeanDefinitionException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionOverrideException;
import io.basc.framework.execution.parameter.ParametersExtractorRegistry;
import io.basc.framework.util.Elements;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultBeanFactory extends AbstractHierarchicalBeanFactory {
	private final Map<String, BeanDefinition> definitionMap = new ConcurrentHashMap<String, BeanDefinition>();
	private final Scope scope;
	private final ParametersExtractorRegistry parametersExtractorRegistry = new BeanFactoryExecutableParametersExtractor(
			this);

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
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		BeanDefinition beanDefinition = getBeanDefinitionOfCache(beanName);
		if (beanDefinition == null) {
			throw new NoSuchBeanDefinitionException(beanName);
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
			if (beanDefinition.getScope().equals(scope)) {
				// 同一个作用域
				registerFactoryBean(beanName, new DefinitionFactoryBean(beanDefinition, parametersExtractorRegistry));
			}
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
	public Scope getScope() {
		return scope;
	}

	@Override
	public Elements<String> getBeanDefinitionNames() {
		return Elements.of(definitionMap.keySet());
	}

	public ParametersExtractorRegistry getParametersExtractorRegistry() {
		return parametersExtractorRegistry;
	}
}
