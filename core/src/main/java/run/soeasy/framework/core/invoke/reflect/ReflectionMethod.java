package run.soeasy.framework.core.invoke.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.invoke.InvodableElement;
import run.soeasy.framework.core.transform.object.Property;
import run.soeasy.framework.core.type.ReflectionUtils;

public class ReflectionMethod extends ReflectionExecutable<Method> implements InvodableElement, Property, Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Class<?> declaringClass;
	private Class<?>[] parameterTypes;

	public ReflectionMethod(@NonNull Method method) {
		super(method);
	}

	@Override
	public void setSource(Method source) {
		synchronized (this) {
			this.name = source.getName();
			this.declaringClass = source.getDeclaringClass();
			this.parameterTypes = source.getParameterTypes();
			super.setSource(source);
		}
	}

	@Override
	public @NonNull Method getSource() {
		Method method = super.getSource();
		if (method == null) {
			synchronized (this) {
				if (method == null) {
					method = ReflectionUtils.findMethod(declaringClass, name, parameterTypes).first();
					super.setSource(method);
				}
			}
		}
		return method;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object invoke(Object target, @NonNull Object... args) {
		return ReflectionUtils.invoke(getSource(), target, args);
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() throws UnsupportedOperationException {
		if (!isWriteable()) {
			throw new UnsupportedOperationException(getSource().toString());
		}
		return getParameterTemplate().first().getReturnTypeDescriptor();
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
