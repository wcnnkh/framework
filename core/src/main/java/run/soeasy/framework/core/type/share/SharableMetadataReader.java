package run.soeasy.framework.core.type.share;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.type.AnnotationMetadata;
import run.soeasy.framework.core.type.ClassMetadata;
import run.soeasy.framework.core.type.classreading.MetadataReader;
import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.io.UrlResource;

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
