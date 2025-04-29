package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.mapping.PropertyTemplate;
import run.soeasy.framework.core.convert.mapping.PropertyTemplate.PropertySourceWrapper;

@Getter
@RequiredArgsConstructor
public class SimpleAnnotationPropertySource<A extends Annotation, P extends PropertyTemplate>
		extends AbstractAnnotationPropertySource<A> implements AnnotationPropertySource<A>, PropertySourceWrapper<P> {
	@NonNull
	private final Class<A> type;
	@NonNull
	private final P source;
}
