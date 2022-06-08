package io.basc.framework.convert.lang;

import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StaticSupplier;

public class ResourceToPropertiesConverter implements Function<Resource, Properties> {
	private final PropertiesResolver propertiesResolver;
	private final Supplier<Charset> charset;

	public ResourceToPropertiesConverter(PropertiesResolver propertiesResolver) {
		this(propertiesResolver, (Charset) null);
	}

	public ResourceToPropertiesConverter(PropertiesResolver propertiesResolver, @Nullable Charset charset) {
		this(propertiesResolver, new StaticSupplier<Charset>(charset));
	}

	public ResourceToPropertiesConverter(PropertiesResolver propertiesResolver, @Nullable Supplier<Charset> charset) {
		this.propertiesResolver = propertiesResolver;
		this.charset = charset;
	}

	public Properties apply(Resource resource) {
		Properties properties = new Properties();
		if (resource.exists()) {
			return properties;
		}
		propertiesResolver.resolveProperties(properties, resource, charset == null ? null : charset.get());
		return properties;
	}

}
