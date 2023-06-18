package io.basc.framework.context.annotation;

import java.io.IOException;

import io.basc.framework.context.config.TypeFilterExtend;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.AnnotationTypeFilter;
import io.basc.framework.core.type.filter.TypeFilter;

public class AnnotationTypeFilterExtend extends AnnotationTypeFilter implements TypeFilterExtend {

	public AnnotationTypeFilterExtend() {
		super(Indexed.class);
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory, TypeFilter chain)
			throws IOException {
		if (match(metadataReader, metadataReaderFactory)) {
			return true;
		}
		return chain.match(metadataReader, metadataReaderFactory);
	}

}
