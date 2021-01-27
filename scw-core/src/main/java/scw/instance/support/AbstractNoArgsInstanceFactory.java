package scw.instance.support;

import scw.core.utils.ClassUtils;
import scw.instance.InstanceUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.util.ClassLoaderProvider;

public abstract class AbstractNoArgsInstanceFactory implements NoArgsInstanceFactory{
	private ClassLoaderProvider classLoaderProvider;
	
	public boolean isPresent(Class<?> clazz) {
		if(clazz == null){
			return false;
		}
		
		return InstanceUtils.isSupported(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Class<T> getClass(String className){
		return (Class<T>) ClassUtils.getClass(className, getClassLoader());
	}
	
	public boolean isPresent(String className) {
		if(className == null){
			return false;
		}
		
		Class<?> clazz = getClass(className);
		if(clazz == null){
			return false;
		}
		
		return isPresent(clazz);
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}
	
	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}
}
