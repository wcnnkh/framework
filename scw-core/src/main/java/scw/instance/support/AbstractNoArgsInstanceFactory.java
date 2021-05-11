package scw.instance.support;

import scw.core.utils.ClassUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.util.ClassLoaderProvider;

public abstract class AbstractNoArgsInstanceFactory implements NoArgsInstanceFactory{
	private ClassLoaderProvider classLoaderProvider;
	
	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}
	
	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}
}
