package io.basc.framework.core.type.share;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.UrlResource;
import lombok.Data;
import lombok.NonNull;

@Data
public class SharableMetadataReader implements MetadataReader {
	private volatile AnnotationMetadata annotationMetadata;
	private volatile ClassMetadata classMetadata;

	private volatile Resource resource;

	@NonNull
	private final Class<?> sourceClass;

	@Override
	public AnnotationMetadata getAnnotationMetadata() {
		if (annotationMetadata == null) {
			synchronized (this) {
				if (annotationMetadata == null) {
					this.annotationMetadata = new SharableAnnotationMetadata(sourceClass);
				}
			}
		}
		return annotationMetadata;
	}

	@Override
	public ClassMetadata getClassMetadata() {
		if (classMetadata == null) {
			synchronized (this) {
				if (classMetadata == null) {
					classMetadata = new SharableClassMetadata(sourceClass);
				}
			}
		}
		return classMetadata;
	}

	@Override
	public Resource getResource() {
		if (resource == null) {
			synchronized (this) {
				if (resource == null) {
					ProtectionDomain protectionDomain = sourceClass.getProtectionDomain();
					if (protectionDomain != null) {
						CodeSource codeSource = protectionDomain.getCodeSource();
						if (codeSource != null) {
							resource = new UrlResource(codeSource.getLocation());
						}
					}
				}
			}
		}
		return resource;
	}

}
