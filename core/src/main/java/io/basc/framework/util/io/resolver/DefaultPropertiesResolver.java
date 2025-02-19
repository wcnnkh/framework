package io.basc.framework.util.io.resolver;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.InvalidPropertiesFormatException;
import java.util.Optional;
import java.util.Properties;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.io.CharsetCapable;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.reflect.ReflectionUtils;
import lombok.NonNull;

public class DefaultPropertiesResolver extends ConfigurablePropertiesResolver {
	private static Logger logger = LogManager.getLogger(DefaultPropertiesResolver.class.getName());
	private static final Method LOAD_METHOD = ReflectionUtils.getDeclaredMethod(Properties.class, "load", Reader.class);

	private static volatile DefaultPropertiesResolver instance;

	public static DefaultPropertiesResolver getInstance() {
		if (instance == null) {
			synchronized (DefaultPropertiesResolver.class) {
				if (instance == null) {
					instance = new DefaultPropertiesResolver();
				}
			}
		}
		return instance;
	}

	public boolean canResolveProperties(Resource resource) {
		if (super.canResolveProperties(resource)) {
			return true;
		}

		if (!resource.exists()) {
			return false;
		}

		return resource.getName().endsWith(".properties");
	}

	public void resolveProperties(Properties properties, Resource resource)
			throws IOException, InvalidPropertiesFormatException {
		if (super.canResolveProperties(resource)) {
			super.resolveProperties(properties, resource);
			return;
		}

		if (!resource.exists()) {
			return;
		}

		if (StringUtils.endsWithIgnoreCase(resource.getName(), ".xml")) {
			resource.getInputStreamPipeline().optional().ifPresent((is) -> properties.loadFromXML(is));
		} else {
			if (LOAD_METHOD == null) {
				logger.warn(
						"The specified character set is only supported in versions of jdk 1.6 and above: " + resource);
				resource.getInputStreamPipeline().optional().ifPresent((is) -> properties.load(is));
			} else {
				resource.toReaderFactory().getReaderPipeline().optional()
						.ifPresent((is) -> ReflectionUtils.invoke(LOAD_METHOD, properties, is));
			}
		}
	}

	@Override
	public void persistenceProperties(@NonNull Properties properties, @NonNull Resource resource) throws IOException {
		if (super.canResolveProperties(resource)) {
			super.persistenceProperties(properties, resource);
			return;
		}

		if (StringUtils.endsWithIgnoreCase(resource.getName(), ".xml")) {
			Optional<String> charsetName = CharsetCapable.getCharsetName(resource);
			resource.getOutputStreamPipeline().optional().ifPresent((os) -> {
				properties.storeToXML(os, null, charsetName.orElse(null));
			});
		} else {
			resource.toWriterFactory().getWriterPipeline().optional().ifPresent((os) -> {
				properties.store(os, null);
			});
		}
	}
}
