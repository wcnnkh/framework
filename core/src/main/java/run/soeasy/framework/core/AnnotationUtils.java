package run.soeasy.framework.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public final class AnnotationUtils {
	public static final Annotation[] EMPTY = new Annotation[0];

	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A getAnnotation(Class<A> annotationType, Annotation... annotations) {
		List<A> list = null;
		for (Annotation ann : annotations) {
			if (ann.annotationType() == annotationType) {
				list = new ArrayList<>(annotations.length);
				list.add((A) ann);
			}
		}
		if (list == null || list.isEmpty()) {
			return null;
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		return SynthesizedAnnotation.synthesize(annotationType, list);
	}
}
