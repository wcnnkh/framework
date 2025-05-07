package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import run.soeasy.framework.core.convert.property.PropertySource;

public interface SynthesizedAnnotation extends Annotation {
	public static <A extends Annotation> A synthesize(Class<A> annotationType, Iterable<? extends A> annotations) {
		return new MergedAnnotation<>(annotationType, annotations).synthesize();
	}

	public static <A extends Annotation> A synthesize(Class<A> annotationType, PropertySource properties) {
		return new SimpleAnnotationPropertySource<>(annotationType, properties).synthesize();
	}
}
