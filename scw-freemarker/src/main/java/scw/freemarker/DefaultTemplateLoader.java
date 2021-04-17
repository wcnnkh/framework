package scw.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import freemarker.cache.TemplateLoader;
import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.io.ResourceUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class DefaultTemplateLoader implements TemplateLoader {
	private static Logger logger = LoggerFactory.getLogger(DefaultTemplateLoader.class);
	private final ResourceLoader resourceLoader;
	
	public DefaultTemplateLoader(ResourceLoader resourceLoader){
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
