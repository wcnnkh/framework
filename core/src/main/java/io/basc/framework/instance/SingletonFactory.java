package io.basc.framework.instance;

import io.basc.framework.lang.Nullable;

public interface SingletonFactory {
	@Nullable
	Object getSingleton(String name);
	
	boolean containsSingleton(String name);

	String[] getSingletonNames();
}
