package scw.core.instance;

import scw.core.utils.ClassUtils;

public abstract class AbstractNoArgsInstanceFactory implements NoArgsInstanceFactory {
	private ClassLoader classLoader;

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		return getInstance((Class<T>) ClassUtils.forNameNullable(name, getClassLoader()));
	}

	public boolean isInstance(String name) {
		return isInstance(ClassUtils.forNameNullable(name, getClassLoader()));
	}

	public boolean isSingleton(String name) {
		if(getClass().getName().endsWith(name)){
			return true;
		}
		
		return false;
	}

	public boolean isSingleton(Class<?> clazz) {
		if(getClass().equals(clazz)){
			return true;
		}
		
		return isSingleton(clazz.getName());
	}
}
