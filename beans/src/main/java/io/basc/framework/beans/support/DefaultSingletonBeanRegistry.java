package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionFactory;
import io.basc.framework.beans.SingletonBeanRegistry;
import io.basc.framework.factory.support.DefaultSingletonRegistry;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Status;

public class DefaultSingletonBeanRegistry extends DefaultSingletonRegistry implements SingletonBeanRegistry{
	private static Logger logger = LoggerFactory.getLogger(DefaultSingletonBeanRegistry.class);
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
	
	public Status<Object> getSingleton(BeanDefinition definition) {
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
					if(logger.isTraceEnabled()){
						logger.trace("destroy {}", definition);
					}
				}
			}
			super.removeSingleton(name);
		}
	}
	
	public void destroyAll() {
		String[] names = getSingletonNames();
		for(int i = names.length - 1; i >= 0; i--){
			removeSingleton(names[i]);
		}
	}

}
