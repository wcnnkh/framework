package scw.beans.support;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionRegistry;
import scw.core.utils.StringUtils;
import scw.lang.AlreadyExistsException;
import scw.util.CollectionFactory;
import scw.util.GenericMap;
import scw.util.alias.DefaultAliasRegistry;

public class DefaultBeanDefinitionRegistry extends DefaultAliasRegistry implements BeanDefinitionRegistry{
	private volatile GenericMap<String, BeanDefinition> beanDefinitionMap = CollectionFactory.createHashMap(true);
	
	public DefaultBeanDefinitionRegistry(){
		super(true);
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
	

	public BeanDefinition registerDefinition(String name,
			BeanDefinition beanDefinition) {
		if (!beanDefinition.getId().equals(name) && !beanDefinition.getNames().contains(name)) {
			registerAlias(beanDefinition.getId(), name);
		}
		
		synchronized (beanDefinitionMap) {
			BeanDefinition definitionToUse = beanDefinition;
			boolean isNew = false;
			if (beanDefinition instanceof DefaultBeanDefinition) {
				DefaultBeanDefinition definition = (DefaultBeanDefinition) beanDefinition;
				if (definition.isNew()) {
					isNew = true;
					definitionToUse = definition.clone();
				}
			}
			
			BeanDefinition registred = beanDefinitionMap.putIfAbsent(definitionToUse.getId(), definitionToUse);
			if(isNew || registred == null){
				if(registred != null){
					throw new AlreadyExistsException(definitionToUse.toString());
				}
				
				for(String alias : definitionToUse.getNames()){
					if(alias.equals(name)){
						continue;
					}
					registerAlias(definitionToUse.getId(), alias);
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
