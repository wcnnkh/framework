package scw.io.resolver;


public interface ConfigurablePropertiesResolver extends PropertiesResolver {
	void addPropertiesResolver(PropertiesResolver propertiesResolver);
}
