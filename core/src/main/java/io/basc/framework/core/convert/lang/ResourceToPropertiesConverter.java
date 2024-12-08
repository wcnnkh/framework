package io.basc.framework.core.convert.lang;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.resolver.PropertiesResolver;

public class ResourceToPropertiesConverter implements Function<Resource, Properties> {
	private final PropertiesResolver propertiesResolver;

	public ResourceToPropertiesConverter(PropertiesResolver propertiesResolver) {
		this.propertiesResolver = propertiesResolver;
	}

	public Properties apply(Resource resource) {
		Properties properties = new Properties();
		if (resource.exists()) {
			return properties;
		}
		try {
			propertiesResolver.resolveProperties(properties, resource);
			return properties;
		} catch (IOException e) {
			throw new ConversionException(resource.getDescription(), e);
		}
	}

}
