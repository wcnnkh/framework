package scw.core.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import scw.core.annotation.AnnotationFactory;
import scw.core.utils.CollectionUtils;

public class SimpleAnnotationFactory implements AnnotationFactory {
	private final Collection<Annotation> annotations;

	public SimpleAnnotationFactory(AnnotatedElement annotatedElement) {
		this.annotations = Arrays.asList(annotatedElement.getDeclaredAnnotations());
	}

	public SimpleAnnotationFactory(Field field) {
		this.annotations = Arrays.asList(field.getDeclaredAnnotations());
	}

	public SimpleAnnotationFactory(Collection<Annotation> annotations) {
		this.annotations = annotations;
	}

	@SuppressWarnings("unchecked")
	public final <T extends Annotation> T getAnnotation(Class<T> type) {
		if (CollectionUtils.isEmpty(annotations)) {
			return null;
		}

		for (Annotation a : annotations) {
			if (a == null) {
				continue;
			}

			if (type.isInstance(a)) {
				return (T) a;
			}
		}
		return null;
	}

}
