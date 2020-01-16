package scw.core.annotation;

import java.lang.annotation.Annotation;

public interface AnnotationFactory {
	<T extends Annotation> T getAnnotation(Class<T> type);
}
