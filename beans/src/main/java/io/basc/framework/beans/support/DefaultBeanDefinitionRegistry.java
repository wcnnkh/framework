package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionRegistry;
import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.alias.DefaultAliasRegistry;

import java.util.HashMap;
import java.util.Map;

public class DefaultBeanDefinitionRegistry extends DefaultAliasRegistry implements BeanDefinitionRegistry{
	private static Logger logger = LoggerFactory.getLogger(DefaultBeanDefinitionRegistry.class);
	private volatile Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();
	
	public DefaultBeanDefinitionRegistry(){
		super(true);
	}
	
	public Object getDefinitionMutex(){
		return beanDefinitionMap;
	}
	
	public BeanDefinition getDefinition(String name) {
		BeanDefinition beanDefinition = beanDefinitionMap.get(name);
		if(beanDefinition == null){
			String[] aliases = getAliases(name);
			if(aliases != null){
				for(String aliase : aliases){
					beanDefinition = beanDefinitionMap.get(aliase);
					if(beanDefinition != null){
						break;
					}
				}
			}
		}
		return beanDefinition;
	}

	public final BeanDefinition getDefinition(Class<?> clazz) {
		return getDefinition(clazz.getName());
	}
	
	public BeanDefinition registerDefinition(BeanDefinition beanDefinition) {
		return registerDefinition(beanDefinition.getId(), beanDefinition);
	}
	
	@Override
	public void registerAlias(String name, String alias) {
		super.registerAlias(name, alias);
		if(logger.isDebugEnabled()){
			logger.debug("register alias {} name {}", alias, name);
		}
	}

	public BeanDefinition registerDefinition(String name,
			BeanDefinition beanDefinition) {
		synchronized (beanDefinitionMap) {
			if (!beanDefinition.getId().equals(name) && !beanDefinition.getNames().contains(name) && !hasAlias(beanDefinition.getId(), name)) {
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
			if(isNew && exist){
				throw new AlreadyExistsException(definitionToUse.toString());
			}
			
			beanDefinitionMap.put(definitionToUse.getId(), definitionToUse);
			if(logger.isDebugEnabled()){
				logger.debug("register [{}] -> definition: {}", name, definitionToUse);
			}
			
			for(String alias : definitionToUse.getNames()){
				if(alias.equals(name)){
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
	
	public String[] getDefinitionIds() {
		return StringUtils.toStringArray(this.beanDefinitionMap.keySet());
	}

	public boolean containsDefinition(String beanName) {
		if(beanDefinitionMap.containsKey(beanName)){
			return true;
		}
		
		String[] aliases = getAliases(beanName);
		for(String name : aliases){
			if(beanDefinitionMap.containsKey(name)){
				return true;
			}
		}
		return false;
	}
}
