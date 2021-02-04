package scw.context.support;

import java.io.IOException;

import scw.context.ClassScanner;
import scw.core.Constants;
import scw.core.type.classreading.MetadataReader;
import scw.core.type.classreading.MetadataReaderFactory;
import scw.core.type.filter.TypeFilter;
import scw.util.ClassLoaderProvider;

public final class SystemContextClassesLoader<S> extends ClassScannerClassesLoader<S> implements TypeFilter{
	private static final String[] IGNORE_PREFIXS = new String[] { "java", "scw.cglib.",
		"scw.asm.", "scw.context.", "scw.util.", "scw.core.", "scw.value.",
		"scw.json.", "scw.io.", "scw.env.", "scw.instance.",
		"scw.convert.", "scw.aop.", "scw.event.", "scw.lang.",
		"scw.mapper.", "scw.logger.", "scw.serializer.", "scw.beans.",
		"scw.boot.", "scw.dom.", "scw.script.", "scw.math.", "scw.net.",
		"scw.http.server.", "scw.configure", "scw.apple.", "scw.data.", "scw.db." };
	
	public SystemContextClassesLoader(ClassScanner classScanner,
			ClassLoaderProvider classLoaderProvider){
		super(classScanner, classLoaderProvider, Constants.SYSTEM_PACKAGE_NAME, null);
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
}
