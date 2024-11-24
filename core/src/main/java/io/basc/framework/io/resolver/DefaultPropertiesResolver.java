package io.basc.framework.io.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.lang.NestedRuntimeException;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.reflect.ReflectionUtils;
import io.basc.framework.util.logging.LogManager;

public class DefaultPropertiesResolver extends PropertiesResolvers {
	private static Logger logger = LogManager.getLogger(DefaultPropertiesResolver.class.getName());
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

	public void resolveProperties(Properties properties, Resource resource, Charset charset) {
		if (super.canResolveProperties(resource)) {
			super.resolveProperties(properties, resource, charset);
			return;
		}

		if (!resource.exists()) {
			return;
		}

		InputStream is = null;
		try {
			is = resource.getInputStream();
			if (resource.getName().endsWith(".xml")) {
				properties.loadFromXML(is);
			} else {
				if (charset == null) {
					properties.load(is);
				} else {
					Method method = ReflectionUtils.getDeclaredMethod(Properties.class, "load", Reader.class);
					if (method == null) {
						logger.warn("The specified character set is only supported in versions of jdk 1.6 and above: "
								+ resource);
						properties.load(is);
					} else {
						InputStreamReader isr = null;
						try {
							isr = new InputStreamReader(is, charset);
							ReflectionUtils.invoke(method, properties, isr);
						} finally {
							if (!resource.isOpen()) {
								IOUtils.close(isr);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new NestedRuntimeException(resource.getDescription(), e);
		} finally {
			if (!resource.isOpen()) {
				IOUtils.closeQuietly(is);
			}
		}
	}

	@Override
	public void persistenceProperties(Properties properties, WritableResource resource, Charset charset) {
		if (super.canResolveProperties(resource)) {
			super.persistenceProperties(properties, resource, charset);
			return;
		}

		try {
			resource.produce((output) -> {
				if (StringUtils.endsWithIgnoreCase(resource.getName(), ".xml")) {
					if (charset == null) {
						properties.storeToXML(output, null);
					} else {
						properties.storeToXML(output, null, charset.name());
					}
				} else {
					if (charset == null) {
						properties.store(output, null);
					} else {
						Writer writer = new OutputStreamWriter(output, charset);
						try {
							properties.store(writer, null);
						} finally {
							writer.close();
						}
					}
				}
			});
		} catch (IOException e) {
			throw new NestedRuntimeException(resource.getDescription(), e);
		}
	}
}
