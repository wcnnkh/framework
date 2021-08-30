package io.basc.framework.core.type.classreading;

import io.basc.framework.core.annotation.AnnotationAttributes;
import io.basc.framework.core.annotation.AnnotationUtils;

class RecursiveAnnotationAttributesVisitor extends AbstractRecursiveAnnotationVisitor {

	protected final String annotationType;


	public RecursiveAnnotationAttributesVisitor(
			String annotationType, AnnotationAttributes attributes, ClassLoader classLoader) {

		super(classLoader, attributes);
		this.annotationType = annotationType;
	}


	@Override
	public void visitEnd() {
		AnnotationUtils.registerDefaultValues(this.attributes);
	}

}
