package shuchaowen.core.application;

import java.util.Collection;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.ConfigurationBeanFactory;
import shuchaowen.core.util.ClassUtils;

public class CommonApplication implements Application {
	private ConfigurationBeanFactory beanFactory;
	private String packageName;

	public CommonApplication() {
		this("");
	}
	
	public CommonApplication(String configPath) {
		this(configPath, "");
	}

	public CommonApplication(String configPath, String packageName) {
		this.packageName = packageName;
		try {
			this.beanFactory = new ConfigurationBeanFactory(null, configPath, packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Collection<Class<?>> getClasses() {
		return ClassUtils.getClasses(packageName);
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
