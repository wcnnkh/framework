package scw.context;

import java.util.Enumeration;

import scw.env.Environment;

public interface ContextEnvironment extends Environment, ProviderClassesLoaderFactory, ProviderLoaderFactory {
	Enumeration<Class<?>> getSourceClasses();
}
