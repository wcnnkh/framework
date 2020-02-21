package scw.embed.servlet;

import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;

public interface ServletContainerInitializerConfiguration {
	Collection<? extends ServletContainerInitializer> getServletContainerInitializers();
	
	Set<Class<?>> getClassSet();
}
