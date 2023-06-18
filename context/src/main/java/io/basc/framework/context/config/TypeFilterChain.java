package io.basc.framework.context.config;

import java.io.IOException;
import java.util.Iterator;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import lombok.Data;

@Data
public class TypeFilterChain implements TypeFilter {
	private final Iterator<? extends TypeFilterExtend> iterator;
	private TypeFilter nextChain;

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		if (iterator.hasNext()) {
			return iterator.next().match(metadataReader, metadataReaderFactory, this);
		}

		return nextChain == null ? false : nextChain.match(metadataReader, metadataReaderFactory);
	}
}
