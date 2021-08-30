package io.basc.framework.type.filter;

import java.io.IOException;

import io.basc.framework.type.ClassMetadata;
import io.basc.framework.type.classreading.MetadataReader;
import io.basc.framework.type.classreading.MetadataReaderFactory;

public abstract class AbstractClassTestingTypeFilter implements TypeFilter {

	public final boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {

		return match(metadataReader.getClassMetadata());
	}

	/**
	 * Determine a match based on the given ClassMetadata object.
	 * @param metadata the ClassMetadata object
	 * @return whether this filter matches on the specified type
	 */
	protected abstract boolean match(ClassMetadata metadata);

}
