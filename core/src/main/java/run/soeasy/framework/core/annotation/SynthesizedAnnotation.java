package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import run.soeasy.framework.core.convert.mapping.PropertyTemplate;

public interface SynthesizedAnnotation extends Annotation {
	public static <A extends Annotation> A synthesize(Class<A> annotationType, Iterable<? extends A> annotations) {
		return new MergedAnnotation<>(annotationType, annotations).synthesize();
	}

	public static <A extends Annotation> A synthesize(Class<A> annotationType, PropertyTemplate properties) {
		return new SimpleAnnotationPropertySource<>(annotationType, properties).synthesize();
	}
}
