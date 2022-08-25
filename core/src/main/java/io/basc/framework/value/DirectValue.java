package io.basc.framework.value;

import java.lang.reflect.Type;

import io.basc.framework.convert.TypeDescriptor;

public abstract class DirectValue extends AbstractValue {

	@Override
	public final <T> T getAsObject(Class<T> type) {
		return super.getAsObject(type);
	}

	@Override
	public final Object getAsObject(Type type) {
		return super.getAsObject(type);
	}

	@Override
	public final Object getAsObject(TypeDescriptor type) {
		Class<?> rawClass = type.getType();
		if (Value.isBaseType(rawClass)) {
			return getAsObject(rawClass);
		}
		return getAsNonBaseType(type);
	}

	protected abstract Object getAsNonBaseType(TypeDescriptor type);
}
