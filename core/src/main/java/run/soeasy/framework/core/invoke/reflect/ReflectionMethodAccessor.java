package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Method;

import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.invoke.field.FieldDescriptor;
import run.soeasy.framework.core.math.NumberValue;
import run.soeasy.framework.core.transform.indexed.IndexedAccessor;

public class ReflectionMethodAccessor extends ReflectionMethod implements FieldDescriptor, IndexedAccessor {
	private static final long serialVersionUID = 1L;
	@Setter
	private Object index;

	public ReflectionMethodAccessor(@NonNull Method method) {
		super(method);
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() {
		return getParameterTemplate().findFirst().get().getReturnTypeDescriptor();
	}

	@Override
	public boolean isWriteable() {
		return getParameterTemplate().getElements().count().compareTo(NumberValue.ONE) == 0;
	}

	@Override
	public boolean isReadable() {
		return getParameterTemplate().isEmpty();
	}

	@Override
	public Object getIndex() {
		return index == null ? super.getName() : index;
	}

	@Override
	public void writeTo(Object value, Object target) {
		invoke(target, value);
	}

	@Override
	public Object readFrom(Object target) {
		return invoke(target, ObjectUtils.EMPTY_ARRAY);
	}

	@Override
	public final Object get() throws ConversionException {
		return readFrom(getTarget());
	}

	@Override
	public final void set(Object value) {
		writeTo(value, getTarget());
	}
}
