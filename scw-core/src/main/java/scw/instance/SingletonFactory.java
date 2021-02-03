package scw.instance;

import scw.lang.Nullable;

public interface SingletonFactory {
	@Nullable
	Object getSingleton(String name);
	
	boolean containsSingleton(String name);

	String[] getSingletonNames();
}
