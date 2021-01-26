package scw.instance.support;

import scw.core.annotation.AnnotationUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.util.ClassLoaderProvider;
import scw.util.JavaVersion;

public abstract class AbstractNoArgsInstanceFactory implements NoArgsInstanceFactory{
	private ClassLoaderProvider classLoaderProvider;
	
	public boolean isPresent(Class<?> clazz) {
		if(clazz == null){
			return false;
		}
		
		if(ClassUtils.isPrimitiveOrWrapper(clazz) || AnnotationUtils.isIgnore(clazz)){
			return false;
		}
		
		return ReflectionUtils.isPresent(clazz) && JavaVersion.isSupported(clazz);
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
