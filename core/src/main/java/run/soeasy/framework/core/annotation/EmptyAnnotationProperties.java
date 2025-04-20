package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.mapping.Properties.EmptyProperties;

@RequiredArgsConstructor
@Getter
public class EmptyAnnotationProperties<A extends Annotation> extends EmptyProperties
		implements AnnotationProperties<A> {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final Class<A> type;
}
