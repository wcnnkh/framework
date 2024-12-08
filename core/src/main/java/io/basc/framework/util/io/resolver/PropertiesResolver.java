package io.basc.framework.util.io.resolver;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.WritableResource;
import lombok.NonNull;

public interface PropertiesResolver {
	boolean canResolveProperties(@NonNull Resource resource);

	void resolveProperties(@NonNull Properties properties, @NonNull Resource resource)
			throws IOException, InvalidPropertiesFormatException;

	void persistenceProperties(@NonNull Properties properties, @NonNull WritableResource resource) throws IOException;
}
