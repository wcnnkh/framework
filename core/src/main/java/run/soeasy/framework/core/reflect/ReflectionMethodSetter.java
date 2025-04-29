package run.soeasy.framework.core.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.invoke.mapping.Setter;

public class ReflectionMethodSetter extends ReflectionMethod implements Setter {
	private String name;

	public ReflectionMethodSetter(@NonNull Method method) {
		super(method);
	}

	@Override
	public String getName() {
		return name == null ? super.getName() : name;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return getParameterDescriptors().findFirst().get().getTypeDescriptor();
	}

	@Override
	public void set(Object target, Object value) {
		try {
			invoke(target, value);
		} catch (Throwable e) {
			ReflectionUtils.handleThrowable(e);
			throw new UndeclaredThrowableException(e);
		}
	}

	@Override
	public ReflectionMethodSetter rename(String name) {
		ReflectionMethodSetter setter = new ReflectionMethodSetter(getSource());
		setter.name = name;
		return setter;
	}

}
