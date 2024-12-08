package io.basc.framework.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import freemarker.cache.TemplateLoader;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.ResourceUtils;
import io.basc.framework.util.io.UnsafeByteArrayInputStream;
import io.basc.framework.util.io.load.ResourceLoader;
import io.basc.framework.util.logging.LogManager;

public class DefaultTemplateLoader implements TemplateLoader {
	private static Logger logger = LogManager.getLogger(DefaultTemplateLoader.class);
	
	private final ResourceLoader resourceLoader;

	public DefaultTemplateLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Object findTemplateSource(String name) throws IOException {
		Resource resource = resourceLoader.getResource(name);
		return (resource == null || !resource.exists()) ? null : resource;
	}

	public long getLastModified(Object templateSource) {
		if (templateSource instanceof Resource) {
			try {
				return ((Resource) templateSource).lastModified();
			} catch (IOException e) {
				logger.error(e, templateSource.toString());
			}
		}
		return 0;
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		if (templateSource instanceof Resource) {
			byte[] data = ResourceUtils.getBytes((Resource) templateSource);
			return new InputStreamReader(new UnsafeByteArrayInputStream(data), encoding);
		}
		return null;
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
		// ignore
	}

}
