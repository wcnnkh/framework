package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.ArrayUtils;

public abstract class AbstractAnnotationPropertyMapping<A extends Annotation> implements AnnotationProperties<A> {

	@Override
	public int hashCode() {
		return getType().hashCode() + ArrayUtils.hashCode(getElements().toList());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj instanceof AnnotationProperties) {
			AnnotationProperties<?> annotationProperties = (AnnotationProperties<?>) obj;
			return getType() == annotationProperties.getType() && getElements()
					.equals(annotationProperties.getElements(), (a, b) -> ObjectUtils.equals(a.getKey(), b.getKey())
							&& ObjectUtils.equals(a.getValue().get(), b.getValue().get()));
		}
		return false;
	}
}
