package io.basc.framework.beans.support;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionRegistry;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ElementSet;
import io.basc.framework.util.Elements;
import io.basc.framework.util.alias.DefaultAliasRegistry;

public class DefaultBeanDefinitionRegistry extends DefaultAliasRegistry implements BeanDefinitionRegistry {
	private static Logger logger = LoggerFactory.getLogger(DefaultBeanDefinitionRegistry.class);
	private volatile Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();

	private BeanDefinition unsafeGetBeanDefinition(String name) {
		BeanDefinition beanDefinition = beanDefinitionMap.get(name);
		if (beanDefinition == null) {
			String[] aliases = getAliases(name);
			if (aliases != null) {
				for (String aliase : aliases) {
					beanDefinition = beanDefinitionMap.get(aliase);
					if (beanDefinition != null) {
						break;
					}
				}
			}
		}
		return beanDefinition;
	}

	public BeanDefinition getBeanDefinition(String name) {
		BeanDefinition beanDefinition = unsafeGetBeanDefinition(name);
		if (beanDefinition == null) {
			synchronized (this) {
				beanDefinition = unsafeGetBeanDefinition(name);
			}
		}
		return beanDefinition;
	}

	@Override
	public void registerAlias(String name, String alias) {
		super.registerAlias(name, alias);
		if (logger.isDebugEnabled()) {
			logger.debug("register alias {} name {}", alias, name);
		}
	}

	public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
		synchronized (this) {
			if (beanDefinitionMap.containsKey(name)) {
				throw new AlreadyExistsException(name);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("register [{}] -> definition: {}", name, beanDefinition);
			}
			beanDefinitionMap.put(name, beanDefinition);
		}
	}

	@Override
	public Elements<String> getBeanDefinitionNames() {
		synchronized (this) {
			Set<String> names = new LinkedHashSet<>(beanDefinitionMap.keySet());
			return new ElementSet<>(names);
		}
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		if (beanDefinitionMap.containsKey(beanName)) {
			return true;
		}

		String[] aliases = getAliases(beanName);
		for (String name : aliases) {
			if (beanDefinitionMap.containsKey(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void removeBeanDefinition(String beanName) {
		synchronized (this) {
			beanDefinitionMap.remove(beanName);
		}
	}
}
