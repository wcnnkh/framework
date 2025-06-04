package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.property.TypedProperties;
import run.soeasy.framework.core.transform.property.TypedPropertiesWrapper;

@Getter
@RequiredArgsConstructor
public class CustomizeAnnotationPropertyMapping<A extends Annotation, P extends TypedProperties>
		extends AbstractAnnotationPropertyMapping<A> implements AnnotationProperties<A>, TypedPropertiesWrapper<P> {
	@NonNull
	private final Class<A> type;
	@NonNull
	private final P source;
}
