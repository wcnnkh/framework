package io.basc.framework.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanDefinitionFactory;
import io.basc.framework.factory.BeanDefinitionRegistry;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.alias.DefaultAliasRegistry;

public class DefaultBeanDefinitionRegistry extends DefaultAliasRegistry implements BeanDefinitionRegistry {
	private static Logger logger = LoggerFactory.getLogger(DefaultBeanDefinitionRegistry.class);
	private volatile Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();
	private BeanDefinitionFactory parentBeanDefinitionFactory;

	@Nullable
	public BeanDefinitionFactory getParentBeanDefinitionFactory() {
		return parentBeanDefinitionFactory;
	}

	public void setParentBeanDefinitionFactory(BeanDefinitionFactory parentBeanDefinitionFactory) {
		this.parentBeanDefinitionFactory = parentBeanDefinitionFactory;
		setParentAliasFactory(parentBeanDefinitionFactory);
	}

	public Object getDefinitionMutex() {
		return beanDefinitionMap;
	}

	public BeanDefinition getDefinition(String name) {
		return getDefinition(name, getParentBeanDefinitionFactory());
	}

	public BeanDefinition getDefinition(String name, BeanDefinitionFactory parent) {
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
		return (beanDefinition != null || parent == null) ? beanDefinition : parent.getDefinition(name);

	}

	public final BeanDefinition getDefinition(Class<?> clazz) {
		return getDefinition(clazz, getParentBeanDefinitionFactory());
	}

	public final BeanDefinition getDefinition(Class<?> clazz, BeanDefinitionFactory parent) {
		BeanDefinition definition = getDefinition(clazz.getName(), null);
		return (definition != null || parent == null) ? definition : parent.getDefinition(clazz);
	}

	public final BeanDefinition registerDefinition(BeanDefinition beanDefinition) {
		return BeanDefinitionRegistry.super.registerDefinition(beanDefinition);
	}

	@Override
	public void registerAlias(String name, String alias) {
		super.registerAlias(name, alias);
		if (logger.isDebugEnabled()) {
			logger.debug("register alias {} name {}", alias, name);
		}
	}

	public BeanDefinition registerDefinition(String name, BeanDefinition beanDefinition) {
		synchronized (beanDefinitionMap) {
			if (!beanDefinition.getId().equals(name) && !beanDefinition.getNames().contains(name)
					&& !hasAlias(beanDefinition.getId(), name)) {
				registerAlias(beanDefinition.getId(), name);
			}

			BeanDefinition definitionToUse = beanDefinition;
			boolean isNew = false;
			if (beanDefinition instanceof DefaultBeanDefinition) {
				DefaultBeanDefinition definition = (DefaultBeanDefinition) beanDefinition;
				if (definition.isNew()) {
					isNew = true;
					definitionToUse = definition.clone();
				}
			}

			boolean exist = beanDefinitionMap.containsKey(definitionToUse.getId());
			if (isNew && exist) {
				throw new AlreadyExistsException(definitionToUse.toString());
			}

			beanDefinitionMap.put(definitionToUse.getId(), definitionToUse);
			if (logger.isDebugEnabled()) {
				logger.debug("register [{}] -> definition: {}", name, definitionToUse);
			}

			for (String alias : definitionToUse.getNames()) {
				if (alias.equals(name)) {
					continue;
				}

				try {
					registerAlias(definitionToUse.getId(), alias);
				} catch (IllegalStateException e) {
					logger.error(e, "register [{}] definition {}", name, definitionToUse);
				}
			}
			return definitionToUse;
		}
	}

	public final String[] getDefinitionIds() {
		return getDefinitionIds(getParentBeanDefinitionFactory());
	}

	public String[] getDefinitionIds(BeanDefinitionFactory parent) {
		String[] ids = StringUtils.toStringArray(this.beanDefinitionMap.keySet());
		return parent == null ? ids : ArrayUtils.merge(ids, parent.getDefinitionIds());
	}

	public final boolean containsDefinition(String beanName) {
		return containsDefinition(beanName, getParentBeanDefinitionFactory());
	}

	public boolean containsDefinition(String beanName, BeanDefinitionFactory parent) {
		if (beanDefinitionMap.containsKey(beanName)) {
			return true;
		}

		String[] aliases = getAliases(beanName);
		for (String name : aliases) {
			if (beanDefinitionMap.containsKey(name)) {
				return true;
			}
		}

		return parent == null ? false : parent.containsDefinition(beanName);
	}
}
