package scw.env;

import java.util.Map;

import scw.convert.ConfigurableConversionService;
import scw.convert.resolve.ConfigurableResourceResolver;
import scw.event.Observable;
import scw.io.ConfigurableResourceLoader;
import scw.io.resolver.ConfigurablePropertiesResolver;
import scw.value.Value;

public interface ConfigurableEnvironment extends Environment, PropertyManager,
		ConfigurableResourceLoader, ConfigurablePropertiesResolver, ConfigurableConversionService, ConfigurableResourceResolver {
	void setWorkPath(String path);
	
	Observable<Map<String, Value>> loadProperties(String resource);

	Observable<Map<String, Value>> loadProperties(String resource, String charsetName);

	Observable<Map<String, Value>> loadProperties(String keyPrefix, String resource, String charsetName);
}
