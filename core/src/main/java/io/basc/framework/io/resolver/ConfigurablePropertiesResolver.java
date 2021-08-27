package io.basc.framework.io.resolver;


public interface ConfigurablePropertiesResolver extends PropertiesResolver, Iterable<PropertiesResolver>{
	void addPropertiesResolver(PropertiesResolver propertiesResolver);
}
