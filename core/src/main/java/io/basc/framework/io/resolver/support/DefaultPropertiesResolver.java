package io.basc.framework.io.resolver.support;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.NestedRuntimeException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Properties;

public class DefaultPropertiesResolver implements PropertiesResolver {
	private static Logger logger = LoggerFactory.getLogger(DefaultPropertiesResolver.class.getName());
	public static final DefaultPropertiesResolver INSTANCE = new DefaultPropertiesResolver();

	public boolean canResolveProperties(Resource resource) {
		String name = resource.getName();
		return name.endsWith(".xml") || name.endsWith(".properties");
	}

	public void resolveProperties(Properties properties, Resource resource, Charset charset) {
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
					Method method = ReflectionUtils.getMethod(Properties.class, "load", Reader.class);
					if (method == null) {
						logger.warn("jdk1.6及以上的版本才支持指定字符集: " + resource.getDescription());
						properties.load(is);
					} else {
						InputStreamReader isr = null;
						try {
							isr = new InputStreamReader(is, charset);
							method.invoke(properties, isr);
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
		try {
			resource.write((output) -> {
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
