package run.soeasy.framework.core.type.share;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.annotation.MergedAnnotations;
import run.soeasy.framework.core.type.AnnotationMetadata;
import run.soeasy.framework.core.type.MethodMetadata;

public class SharableAnnotationMetadata extends SharableClassMetadata implements AnnotationMetadata {
	private volatile MergedAnnotations mergedAnnotations;

	public SharableAnnotationMetadata(@NonNull Class<?> sourceClass) {
		super(sourceClass);
	}

	@Override
	public MergedAnnotations getAnnotations() {
		if (mergedAnnotations == null) {
			synchronized (this) {
				if (mergedAnnotations == null) {
					mergedAnnotations = MergedAnnotations.from(getSourceClass());
				}
			}
		}
		return mergedAnnotations;
	}

	@Override
	public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
		Method[] methods = getSourceClass().getDeclaredMethods();
		if (methods == null) {
			return Collections.emptySet();
		}

		return Arrays.asList(methods).stream().map(SharableMethodMetadata::new)
				.filter((e) -> e.isAnnotated(annotationName)).collect(Collectors.toSet());
	}
}
