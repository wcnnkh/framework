package scw.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Collection;

import scw.core.utils.CollectionUtils;

public class MultipleAnnotationFactory implements AnnotationFactory {
	private Collection<? extends AnnotationFactory> annotationFactories;

	public MultipleAnnotationFactory(Collection<? extends AnnotationFactory> annotationFactories) {
		this.annotationFactories = annotationFactories;
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		if (CollectionUtils.isEmpty(annotationFactories)) {
			return null;
		}

		for (AnnotationFactory annotationFactory : annotationFactories) {
			T t = annotationFactory.getAnnotation(type);
			if (t != null) {
				return t;
			}
		}
		return null;
	}
}
