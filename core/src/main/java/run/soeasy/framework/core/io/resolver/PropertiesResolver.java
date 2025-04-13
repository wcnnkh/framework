package run.soeasy.framework.core.io.resolver;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import lombok.NonNull;
import run.soeasy.framework.core.io.Resource;

public interface PropertiesResolver {
	boolean canResolveProperties(@NonNull Resource resource);

	void resolveProperties(@NonNull Properties properties, @NonNull Resource resource)
			throws IOException, InvalidPropertiesFormatException;

	void persistenceProperties(@NonNull Properties properties, @NonNull Resource resource) throws IOException;
}
