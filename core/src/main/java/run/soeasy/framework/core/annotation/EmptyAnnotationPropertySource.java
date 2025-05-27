package run.soeasy.framework.core.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Iterator;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.property.PropertyAccessor;

@RequiredArgsConstructor
@Getter
public class EmptyAnnotationPropertySource<A extends Annotation> implements AnnotationProperties<A>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final Class<A> type;

	@Override
	public Iterator<PropertyAccessor> iterator() {
		return Collections.emptyIterator();
	}
}
