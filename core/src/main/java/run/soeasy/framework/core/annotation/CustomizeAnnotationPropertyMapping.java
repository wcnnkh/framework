package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.property.PropertyMapping;
import run.soeasy.framework.core.transform.property.PropertyMappingWrapper;

@Getter
@RequiredArgsConstructor
public class CustomizeAnnotationPropertyMapping<A extends Annotation, P extends PropertyMapping> extends
		AbstractAnnotationPropertyMapping<A> implements AnnotationPropertyMapping<A>, PropertyMappingWrapper<P> {
	@NonNull
	private final Class<A> type;
	@NonNull
	private final P source;
}
