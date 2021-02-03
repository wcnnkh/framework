package scw.beans.support;

import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionRegistry;
import scw.core.utils.StringUtils;
import scw.lang.AlreadyExistsException;
import scw.util.alias.DefaultAliasRegistry;

public class DefaultBeanDefinitionRegistry extends DefaultAliasRegistry implements BeanDefinitionRegistry{
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
	

	public BeanDefinition registerDefinition(String name,
			BeanDefinition beanDefinition) {
		if (!beanDefinition.getId().equals(name) && !beanDefinition.getNames().contains(name)) {
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
		
		synchronized (beanDefinitionMap) {
			exist = beanDefinitionMap.containsKey(definitionToUse.getId());
			if(isNew && exist){
				throw new AlreadyExistsException(definitionToUse.toString());
			}
			
			beanDefinitionMap.put(definitionToUse.getId(), definitionToUse);
			for(String alias : definitionToUse.getNames()){
				if(alias.equals(name)){
					continue;
				}
				registerAlias(definitionToUse.getId(), alias);
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
