package shuchaowen.core.application;

import java.util.Collection;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.ConfigurationBeanFactory;
import shuchaowen.core.util.ClassUtils;

public class CommonApplication implements Application {
	private ConfigurationBeanFactory beanFactory;

	public CommonApplication() {
		this("");
	}
	
	public void registerSingleton(Class<?> type, Object bean){
		beanFactory.registerSingleton(type, bean);
	}
	
	public void registerSingleton(String name, Object bean){
		beanFactory.registerSingleton(name, bean);
	}
	
	public CommonApplication(String configPath) {
		try {
			this.beanFactory = new ConfigurationBeanFactory(configPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Collection<Class<?>> getClasses() {
		return ClassUtils.getClasses(beanFactory.getPackageNames());
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void init() {
		beanFactory.init();
	}

	public void destroy() {
		beanFactory.destroy();
	}
}
