package run.soeasy.framework.core;

import java.lang.annotation.Annotation;

import run.soeasy.framework.util.ObjectUtils;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.collections.ArrayUtils;

public abstract class AbstractAnnotationProperties<A extends Annotation> implements AnnotationProperties<A> {

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
			return getType() == annotationProperties.getType()
					&& equals(annotationProperties, (a, b) -> StringUtils.equals(a.getKey(), b.getKey())
							&& ObjectUtils.equals(a.getValue().get(), b.getValue().get()));
		}
		return false;
	}
}
