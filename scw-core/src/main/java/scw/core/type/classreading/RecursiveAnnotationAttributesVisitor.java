package scw.core.type.classreading;

import scw.core.annotation.AnnotationAttributes;
import scw.core.annotation.AnnotationUtils;

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
