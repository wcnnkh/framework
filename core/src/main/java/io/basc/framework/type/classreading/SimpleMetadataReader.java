package io.basc.framework.type.classreading;

import io.basc.framework.io.Resource;
import io.basc.framework.lang.NestedIOException;
import io.basc.framework.type.AnnotationMetadata;
import io.basc.framework.type.ClassMetadata;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;

/**
 * {@link MetadataReader} implementation based on an ASM
 *
 * <p>Package-visible in order to allow for repackaging the ASM library
 * without effect on users of the {@code core.type} package.
 */
final class SimpleMetadataReader implements MetadataReader {

	private final Resource resource;

	private final ClassMetadata classMetadata;

	private final AnnotationMetadata annotationMetadata;


	SimpleMetadataReader(Resource resource, ClassLoader classLoader) throws IOException {
		InputStream is = new BufferedInputStream(resource.getInputStream());
		ClassReader classReader;
		try {
			classReader = new ClassReader(is);
		}
		catch (IllegalArgumentException ex) {
			throw new NestedIOException("ASM ClassReader failed to parse class file - " +
					"probably due to a new Java class file version that isn't supported yet: " + resource, ex);
		}
		finally {
			is.close();
		}

		AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor(classLoader);
		classReader.accept(visitor, ClassReader.SKIP_DEBUG);

		this.annotationMetadata = visitor;
		// (since AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor)
		this.classMetadata = visitor;
		this.resource = resource;
	}


	public Resource getResource() {
		return this.resource;
	}

	public ClassMetadata getClassMetadata() {
		return this.classMetadata;
	}

	public AnnotationMetadata getAnnotationMetadata() {
		return this.annotationMetadata;
	}

}
