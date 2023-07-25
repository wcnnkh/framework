package io.basc.framework.freemarker;

import java.io.IOException;
import java.io.Reader;

import freemarker.cache.TemplateLoader;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.lang.UnsupportedException;

public class TemplateLoaders extends ConfigurableServices<TemplateLoader> implements TemplateLoader {

	public Object findTemplateSource(String name) throws IOException {
		for (TemplateLoader loader : getServices()) {
			Object resource = loader.findTemplateSource(name);
			if (resource != null) {
				return new InternalTemplateLoaderSource(resource, loader);
			}
		}
		return null;
	}

	public long getLastModified(Object templateSource) {
		if (templateSource instanceof InternalTemplateLoaderSource) {
			return ((InternalTemplateLoaderSource) templateSource).getLastModified();
		}
		throw new UnsupportedException(templateSource.getClass().getName());
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		if (templateSource instanceof InternalTemplateLoaderSource) {
			return ((InternalTemplateLoaderSource) templateSource).getReader(encoding);
		}
		throw new UnsupportedException(templateSource.getClass().getName() + ", encoding=" + encoding);
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource instanceof InternalTemplateLoaderSource) {
			((InternalTemplateLoaderSource) templateSource).closeTemplateSource();
			return;
		}
		throw new UnsupportedException(templateSource.getClass().getName());
	}

	private static final class InternalTemplateLoaderSource {
		private final Object templateSource;
		private final TemplateLoader templateLoader;

		public InternalTemplateLoaderSource(Object templateSource, TemplateLoader templateLoader) {
			this.templateSource = templateSource;
			this.templateLoader = templateLoader;
		}

		public long getLastModified() {
			return templateLoader.getLastModified(templateSource);
		}

		public Reader getReader(String encoding) throws IOException {
			return templateLoader.getReader(templateSource, encoding);
		}

		public void closeTemplateSource() throws IOException {
			templateLoader.closeTemplateSource(templateSource);
		}

		@Override
		public String toString() {
			return templateSource.toString();
		}
	}
}
