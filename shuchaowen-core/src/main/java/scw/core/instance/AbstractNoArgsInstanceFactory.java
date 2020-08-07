package scw.core.instance;

import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.util.Accept;

public abstract class AbstractNoArgsInstanceFactory implements NoArgsInstanceFactory, Accept<Class<?>> {
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
	
	public boolean accept(Class<?> clazz) {
		return !ClassUtils.isPrimitiveOrWrapper(clazz) && ReflectionUtils.isPresent(clazz);
	}
}
