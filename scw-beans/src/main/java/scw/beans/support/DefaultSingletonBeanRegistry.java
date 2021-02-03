package scw.beans.support;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionFactory;
import scw.beans.SingletonBeanRegistry;
import scw.instance.support.DefaultSingletonRegistry;
import scw.util.Result;

public class DefaultSingletonBeanRegistry extends DefaultSingletonRegistry implements SingletonBeanRegistry{
	private final BeanDefinitionFactory beanDefinitionFactory;
	
	public DefaultSingletonBeanRegistry(BeanDefinitionFactory beanDefinitionFactory){
		this.beanDefinitionFactory = beanDefinitionFactory;
	}
	
	@Override
	public Object getSingleton(String beanName) {
		Object instance = super.getSingleton(beanName);
		if(instance != null){
			return instance;
		}

		BeanDefinition definition = beanDefinitionFactory.getDefinition(beanName);
		if(definition == null){
			return false;
		}
		
		return super.getSingleton(definition.getId());
	}
	
	@Override
	public boolean containsSingleton(String beanName) {
		if(super.containsSingleton(beanName)){
			return true;
		}
		
		BeanDefinition definition = beanDefinitionFactory.getDefinition(beanName);
		if(definition == null){
			return false;
		}
		
		return super.containsSingleton(definition.getId());
	}
	
	public Result<Object> getSingleton(BeanDefinition definition) {
		return getSingleton(definition.getId(), definition);
	}
	
	@Override
	public void removeSingleton(String name) {
		synchronized (getSingletonMutex()) {
			BeanDefinition definition = beanDefinitionFactory.getDefinition(name);
			if(definition != null){
				Object instance = getSingleton(definition.getId());
				if(instance != null){
					definition.destroy(instance);
				}
			}
		}
		super.removeSingleton(name);
	}
	
	public void destroyAll() {
		String[] names = getSingletonNames();
		for(int i = names.length - 1; i >= 0; i--){
			removeSingleton(names[i]);
		}
	}

}
