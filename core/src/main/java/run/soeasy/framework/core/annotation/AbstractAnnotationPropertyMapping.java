package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.ArrayUtils;

public abstract class AbstractAnnotationPropertyMapping<A extends Annotation> implements AnnotationPropertyMapping<A> {

	@Override
	public int hashCode() {
		return getType().hashCode() + ArrayUtils.hashCode(getElements().toList());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj instanceof AnnotationPropertyMapping) {
			AnnotationPropertyMapping<?> annotationProperties = (AnnotationPropertyMapping<?>) obj;
			return getType() == annotationProperties.getType() && getElements()
					.equals(annotationProperties.getElements(), (a, b) -> ObjectUtils.equals(a.getKey(), b.getKey())
							&& ObjectUtils.equals(a.getValue().get(), b.getValue().get()));
		}
		return false;
	}
}
