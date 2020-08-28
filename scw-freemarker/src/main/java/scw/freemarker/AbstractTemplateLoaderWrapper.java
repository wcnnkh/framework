package scw.freemarker;

import java.io.IOException;
import java.io.Reader;

import freemarker.cache.TemplateLoader;

public abstract class AbstractTemplateLoaderWrapper implements TemplateLoader {

	public abstract TemplateLoader getTemplateLoader();

	public Object findTemplateSource(String name) throws IOException {
		return getTemplateLoader().findTemplateSource(name);
	}

	public long getLastModified(Object templateSource) {
		return getTemplateLoader().getLastModified(templateSource);
	}

	public Reader getReader(Object templateSource, String encoding)
			throws IOException {
		return getTemplateLoader().getReader(templateSource, encoding);
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
		getTemplateLoader().closeTemplateSource(templateSource);
	}

}
