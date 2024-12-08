package io.basc.framework.core.type.classreading;

import java.io.IOException;

import org.objectweb.asm.ClassReader;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.lang.NestedIOException;
import io.basc.framework.util.io.Resource;

/**
 * {@link MetadataReader} implementation based on an ASM
 * {@link io.basc.framework.asm.ClassReader}.
 *
 * @author Juergen Hoeller
 * @author Costin Leau
 */
final class SimpleMetadataReader implements MetadataReader {

	private static final int PARSING_OPTIONS = ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES;

	private final Resource resource;

	private final AnnotationMetadata annotationMetadata;

	SimpleMetadataReader(Resource resource, ClassLoader classLoader) throws IOException {
		SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor(classLoader);
		getClassReader(resource).accept(visitor, PARSING_OPTIONS);
		this.resource = resource;
		this.annotationMetadata = visitor.getMetadata();
	}

	private static ClassReader getClassReader(Resource resource) throws IOException {
		return resource.getInputStream().export().map((is) -> {
			try {
				return new ClassReader(is);
			} catch (IllegalArgumentException ex) {
				throw new NestedIOException(
						"ASM ClassReader failed to parse class file - "
								+ "probably due to a new Java class file version that isn't supported yet: " + resource,
						ex);
			}
		}).get();
	}

	@Override
	public Resource getResource() {
		return this.resource;
	}

	@Override
	public ClassMetadata getClassMetadata() {
		return this.annotationMetadata;
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata() {
		return this.annotationMetadata;
	}

}
