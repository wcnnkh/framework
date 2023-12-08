package io.basc.framework.core.type.filter;

import java.io.IOException;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;

/**
 * Base interface for type filters using a
 * {@link io.basc.framework.core.type.classreading.MetadataReader}.
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Mark Fisher
 */
@FunctionalInterface
public interface TypeFilter {

	/**
	 * Determine whether this filter matches for the class described by the given
	 * metadata.
	 * 
	 * @param metadataReader        the metadata reader for the target class
	 * @param metadataReaderFactory a factory for obtaining metadata readers for
	 *                              other classes (such as superclasses and
	 *                              interfaces)
	 * @return whether this filter matches
	 * @throws IOException in case of I/O failure when reading metadata
	 */
	boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException;

	default TypeFilter and(TypeFilter typeFilter) {
		return (m, f) -> this.match(m, f) && typeFilter.match(m, f);
	}

	default TypeFilter or(TypeFilter typeFilter) {
		return (m, f) -> this.match(m, f) || typeFilter.match(m, f);
	}
}
