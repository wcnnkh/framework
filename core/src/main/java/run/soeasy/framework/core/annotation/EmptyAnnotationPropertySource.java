package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.mapping.PropertySource.EmptyPropertySource;

@RequiredArgsConstructor
@Getter
public class EmptyAnnotationPropertySource<A extends Annotation> extends EmptyPropertySource
		implements AnnotationPropertySource<A> {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final Class<A> type;
}
