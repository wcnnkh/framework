package run.soeasy.framework.io.resolver;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.domain.CharsetCapable;
import run.soeasy.framework.core.type.ReflectionUtils;
import run.soeasy.framework.io.Resource;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

public class DefaultPropertiesResolver extends ConfigurablePropertiesResolver {
	private static Logger logger = LogManager.getLogger(DefaultPropertiesResolver.class.getName());
	private static final Method LOAD_METHOD = ReflectionUtils.findDeclaredMethod(Properties.class, "load", Reader.class)
			.first();

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
				resource.getReaderPipeline().optional()
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
			String charsetName = CharsetCapable.getCharsetName(resource);
			resource.getOutputStreamPipeline().optional().ifPresent((os) -> {
				properties.storeToXML(os, null, charsetName);
			});
		} else {
			resource.getWriterPipeline().optional().ifPresent((os) -> {
				properties.store(os, null);
			});
		}
	}
}
