package io.basc.framework.freemarker;

import io.basc.framework.lang.UnsupportedException;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

import freemarker.cache.TemplateLoader;

public class MultiTemplateLoader extends LinkedList<TemplateLoader> implements TemplateLoader {
	private static final long serialVersionUID = 1L;

	public Object findTemplateSource(String name) throws IOException {
		for (TemplateLoader loader : this) {
			Object resource = loader.findTemplateSource(name);
			if (resource != null) {
				return new MultiTemplateLoaderSource(resource, loader);
			}
		}
		return null;
	}

	public long getLastModified(Object templateSource) {
		if (templateSource instanceof MultiTemplateLoaderSource) {
			return ((MultiTemplateLoaderSource) templateSource).getLastModified();
		}
		throw new UnsupportedException(templateSource.getClass().getName());
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		if (templateSource instanceof MultiTemplateLoaderSource) {
			return ((MultiTemplateLoaderSource) templateSource).getReader(encoding);
		}
		throw new UnsupportedException(templateSource.getClass().getName() + ", encoding=" + encoding);
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource instanceof MultiTemplateLoaderSource) {
			((MultiTemplateLoaderSource) templateSource).closeTemplateSource();
			return;
		}
		throw new UnsupportedException(templateSource.getClass().getName());
	}

	public static final class MultiTemplateLoaderSource {
		private final Object templateSource;
		private final TemplateLoader templateLoader;

		public MultiTemplateLoaderSource(Object templateSource, TemplateLoader templateLoader) {
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
	}
}
