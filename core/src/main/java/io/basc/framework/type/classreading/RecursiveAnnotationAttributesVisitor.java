package io.basc.framework.type.classreading;

import io.basc.framework.annotation.AnnotationAttributes;
import io.basc.framework.annotation.AnnotationUtils;

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
