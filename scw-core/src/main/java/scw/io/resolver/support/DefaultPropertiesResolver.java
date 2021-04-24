package scw.io.resolver.support;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Properties;

import scw.core.reflect.ReflectionUtils;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.io.resolver.PropertiesResolver;
import scw.lang.NestedRuntimeException;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class DefaultPropertiesResolver implements PropertiesResolver{
	private static Logger logger = LoggerFactory.getLogger(DefaultPropertiesResolver.class.getName());
	
	public static final DefaultPropertiesResolver INSTANCE = new DefaultPropertiesResolver();
	
	public boolean canResolveProperties(Resource resource) {
		String name = resource.getName();
		return name.endsWith(".xml") || name.endsWith(".properties");
	}
	
	public void resolveProperties(Properties properties, Resource resource,
			Charset charset) {
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
					Method method = ReflectionUtils.getMethod(Properties.class,
							"load", Reader.class);
					if (method == null) {
						logger.warn("jdk1.6及以上的版本才支持指定字符集: "
								+ resource.getDescription());
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
				IOUtils.close(is);
			}
		}
	}
}
