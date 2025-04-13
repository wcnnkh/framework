package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.stereotype.Properties;
import run.soeasy.framework.core.transform.stereotype.Properties.PropertiesWrapper;

@Getter
@RequiredArgsConstructor
public class SimpleAnnotationProperties<A extends Annotation, P extends Properties>
		extends AbstractAnnotationProperties<A> implements AnnotationProperties<A>, PropertiesWrapper<P> {
	@NonNull
	private final Class<A> type;
	@NonNull
	private final P source;
}
