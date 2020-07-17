package scw.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import freemarker.cache.TemplateLoader;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.io.UnsafeByteArrayInputStream;

public class MyTemplateLoader implements TemplateLoader {

	public Object findTemplateSource(String name) throws IOException {
		return ResourceUtils.getResourceOperations().getResource(name);
	}

	public long getLastModified(Object templateSource) {
		if (templateSource instanceof Resource) {
			try {
				return ((Resource) templateSource).lastModified();
			} catch (IOException e) {
				e.printStackTrace();
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
