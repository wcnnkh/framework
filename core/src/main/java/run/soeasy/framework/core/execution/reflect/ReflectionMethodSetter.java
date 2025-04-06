package run.soeasy.framework.core.execution.reflect;

import java.lang.reflect.Method;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execution.Setter;
import run.soeasy.framework.lang.ImpossibleException;
import run.soeasy.framework.util.reflect.ReflectionUtils;

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
			throw new ImpossibleException();
		}
	}

	@Override
	public ReflectionMethodSetter rename(String name) {
		ReflectionMethodSetter setter = new ReflectionMethodSetter(getSource());
		setter.name = name;
		return setter;
	}

}
