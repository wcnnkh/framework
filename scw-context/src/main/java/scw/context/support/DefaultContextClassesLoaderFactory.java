package scw.context.support;

import java.io.IOException;

import scw.context.ClassesLoader;
import scw.context.ConfigurableClassesLoader;
import scw.context.ContextClassesLoaderFactory;
import scw.core.Constants;
import scw.core.type.classreading.MetadataReader;
import scw.core.type.classreading.MetadataReaderFactory;

public class DefaultContextClassesLoaderFactory extends
		DefaultClassesLoaderFactory implements ContextClassesLoaderFactory {
	private static final String[] IGNORE_PREFIXS = new String[] { "java", "scw.cglib.",
			"scw.asm.", "scw.context.", "scw.util.", "scw.core.", "scw.value.",
			"scw.json.", "scw.io.", "scw.env.", "scw.instance.",
			"scw.convert.", "scw.aop.", "scw.event.", "scw.lang.",
			"scw.mapper.", "scw.logger.", "scw.serializer.", "scw.beans.",
			"scw.boot.", "scw.dom.", "scw.script.", "scw.math.", "scw.net.",
			"scw.http.server." };
	@SuppressWarnings("rawtypes")
	private final DefaultClassesLoader<?> contextClassesLoader = new DefaultClassesLoader();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DefaultContextClassesLoaderFactory(boolean cache) {
		super(cache);
		contextClassesLoader
				.add((ClassesLoader) getClassesLoader(Constants.SYSTEM_PACKAGE_NAME));
	}

	@Override
	public boolean match(MetadataReader metadataReader,
			MetadataReaderFactory metadataReaderFactory) throws IOException {
		String className = metadataReader.getClassMetadata().getClassName();
		for (String prefix : IGNORE_PREFIXS) {
			if (className.startsWith(prefix)) {
				return false;
			}
		}
		return super.match(metadataReader, metadataReaderFactory);
	}

	public ConfigurableClassesLoader<?> getContextClassesLoader() {
		return contextClassesLoader;
	}
}
