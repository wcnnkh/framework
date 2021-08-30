package io.basc.framework.instance.support;

import io.basc.framework.instance.NoArgsInstanceFactory;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;

public abstract class AbstractNoArgsInstanceFactory implements NoArgsInstanceFactory{
	private ClassLoaderProvider classLoaderProvider;
	
	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}
	
	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}
}
