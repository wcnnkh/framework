package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Method;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.Property;

public class ReflectionPropertyMethod extends ReflectionMethod implements Property {
	private static final long serialVersionUID = 1L;

	public ReflectionPropertyMethod(@NonNull Method method) {
		super(method);
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() throws UnsupportedOperationException {
		if (!isReadable()) {
			throw new UnsupportedOperationException(getSource().toString());
		}
		return super.getReturnTypeDescriptor();
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() throws UnsupportedOperationException {
		if (!isWriteable()) {
			throw new UnsupportedOperationException(getSource().toString());
		}
		TypeDescriptor typeDescriptor = getParameterTemplate().first().getReturnTypeDescriptor();
		return new TypeDescriptor(typeDescriptor.getResolvableType(), null);
	}

	@Override
	public boolean isReadable() {
		return getParameterTemplate().isEmpty();
	}

	@Override
	public boolean isWriteable() {
		return getParameterTemplate().count() == 1;
	}

	@Override
	public Object readFrom(Object target) throws UnsupportedOperationException {
		if (!isReadable()) {
			throw new UnsupportedOperationException(getSource().toString());
		}
		return invoke(target, ArrayUtils.EMPTY_OBJECT_ARRAY);
	}

	@Override
	public void writeTo(Object value, Object target) throws UnsupportedOperationException {
		if (!isWriteable()) {
			throw new UnsupportedOperationException(getSource().toString());
		}
		invoke(target, value);
	}

}
