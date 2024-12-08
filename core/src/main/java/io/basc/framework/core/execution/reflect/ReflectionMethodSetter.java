package io.basc.framework.core.execution.reflect;

import java.lang.reflect.Method;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Setter;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ShouldNeverGetHereException;
import io.basc.framework.util.reflect.ReflectionUtils;
import lombok.NonNull;

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
			execute(target, Elements.singleton(value));
		} catch (Throwable e) {
			ReflectionUtils.handleThrowable(e);
			throw new ShouldNeverGetHereException();
		}
	}

	@Override
	public ReflectionMethodSetter rename(String name) {
		ReflectionMethodSetter setter = new ReflectionMethodSetter(getMember());
		setter.name = name;
		return setter;
	}

}
