package run.soeasy.framework.core.spi;

import lombok.Getter;
import run.soeasy.framework.core.comparator.TypeComparator;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.type.ClassUtils;

@Getter
public class TypeServiceMap<S> extends ServiceMap<Class<?>, S> {

	public TypeServiceMap() {
		super(TypeComparator.DEFAULT);
	}

	public Streamable<S> assignableFrom(Class<?> requiredType) {
		S service = getDelegate().get(requiredType);
		if (service != null) {
			return Streamable.singleton(service);
		}

		return filter((e) -> ClassUtils.isAssignable(e.getKey(), requiredType)).map((e) -> e.getValue());
	}

}
