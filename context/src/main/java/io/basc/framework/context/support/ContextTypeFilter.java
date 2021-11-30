package io.basc.framework.context.support;

import java.io.IOException;

import io.basc.framework.context.annotation.EnableConditionUtils;
import io.basc.framework.context.annotation.Indexed;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.env.Environment;
import io.basc.framework.lang.Ignore;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.JavaVersion;

public class ContextTypeFilter implements TypeFilter {
	private final Environment environment;

	public ContextTypeFilter(@Nullable Environment environment) {
		this.environment = environment;
	}

	@Nullable
	public Environment getEnvironment() {
		return environment;
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		if (classMetadata.isEnum() || classMetadata.isAnnotation()) {
			return false;
		}

		AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
		if (annotationMetadata.hasAnnotation(Ignore.class.getName())) {
			return false;
		}

		if ((annotationMetadata.getAnnotationTypes().isEmpty() || (annotationMetadata.getAnnotationTypes().size() == 1
				&& annotationMetadata.hasAnnotation(FunctionalInterface.class.getName())))
				&& !annotationMetadata.hasAnnotatedMethods(Indexed.class.getName())
				&& !annotationMetadata.hasMetaAnnotation(Indexed.class.getName())) {
			return false;
		}

		return classMetadata.isPublic() && JavaVersion.isSupported(annotationMetadata)
				&& (environment == null || EnableConditionUtils.enable(metadataReader, environment));
	}
}
