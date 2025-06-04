package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import run.soeasy.framework.core.transform.property.TypedProperties;

public interface SynthesizedAnnotation extends Annotation {
	public static <A extends Annotation> A synthesize(Class<A> annotationType, Iterable<? extends A> annotations) {
		return new MergedAnnotation<>(annotationType, annotations).synthesize();
	}

	public static <A extends Annotation> A synthesize(Class<A> annotationType, TypedProperties properties) {
		return new CustomizeAnnotationPropertyMapping<>(annotationType, properties).synthesize();
	}
}
