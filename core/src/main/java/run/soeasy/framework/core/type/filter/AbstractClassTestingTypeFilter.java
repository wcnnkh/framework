package run.soeasy.framework.core.type.filter;

import java.io.IOException;

import run.soeasy.framework.core.type.ClassMetadata;
import run.soeasy.framework.core.type.classreading.MetadataReader;
import run.soeasy.framework.core.type.classreading.MetadataReaderFactory;

/**
 * Type filter that exposes a {@link run.soeasy.framework.core.type.ClassMetadata}
 * object to subclasses, for class testing purposes.
 *
 * @author Rod Johnson
 * @author Costin Leau
 * @author Juergen Hoeller
 * @see #match(run.soeasy.framework.core.type.ClassMetadata)
 */
public abstract class AbstractClassTestingTypeFilter implements TypeFilter {

	@Override
	public final boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {

		return match(metadataReader.getClassMetadata());
	}

	/**
	 * Determine a match based on the given ClassMetadata object.
	 * 
	 * @param metadata the ClassMetadata object
	 * @return whether this filter matches on the specified type
	 */
	protected abstract boolean match(ClassMetadata metadata);

}
