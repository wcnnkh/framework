package io.basc.framework.io.resolver;

import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.lang.Nullable;

public interface PropertiesResolver {
	boolean canResolveProperties(Resource resource);

	void resolveProperties(Properties properties, Resource resource, @Nullable Charset charset);

	void persistenceProperties(Properties properties, WritableResource resource, @Nullable Charset charset);
}
