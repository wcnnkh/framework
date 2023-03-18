package io.basc.framework.context.support;

import io.basc.framework.context.annotation.EnableConditionUtils;
import io.basc.framework.context.annotation.Indexed;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.filter.AnnotationTypeFilter;
import io.basc.framework.lang.Ignore;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.JavaVersion;
import io.basc.framework.value.ValueFactory;

public class ContextTypeFilter extends AnnotationTypeFilter {
	private final ValueFactory<String> propertyFactory;

	public ContextTypeFilter(@Nullable ValueFactory<String> propertyFactory) {
		super(Indexed.class);
		this.propertyFactory = propertyFactory;
	}

	@Override
	protected boolean matchSelf(MetadataReader metadataReader) {
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		if (classMetadata.isEnum() || classMetadata.isAnnotation()) {
			return false;
		}

		AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
		if (annotationMetadata.hasAnnotation(Ignore.class.getName())) {
			return false;
		}

		return super.matchSelf(metadataReader) && classMetadata.isPublic()
				&& JavaVersion.isSupported(annotationMetadata)
				&& (propertyFactory == null || EnableConditionUtils.enable(metadataReader, propertyFactory));
	}
}
