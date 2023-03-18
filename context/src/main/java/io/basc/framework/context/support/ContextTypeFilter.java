package io.basc.framework.context.support;

import java.io.IOException;

import io.basc.framework.context.annotation.EnableConditionUtils;
import io.basc.framework.context.annotation.Indexed;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Ignore;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.JavaVersion;
import io.basc.framework.value.ValueFactory;

public class ContextTypeFilter implements TypeFilter {
	private final ValueFactory<String> propertyFactory;

	public ContextTypeFilter(@Nullable ValueFactory<String> propertyFactory) {
		this.propertyFactory = propertyFactory;
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
				&& (propertyFactory == null || EnableConditionUtils.enable(metadataReader, propertyFactory));
	}
}
