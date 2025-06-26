package run.soeasy.framework.core.transform.property;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ObjectPropertyAccessor<T extends Property> implements PropertyAccessor, PropertyWrapper<T> {
	@NonNull
	private final T source;
	private final Object target;

	@Override
	public Object get() {
		return source.readFrom(target);
	}

	@Override
	public void set(Object value) {
		source.writeTo(target, value);
	}
}
