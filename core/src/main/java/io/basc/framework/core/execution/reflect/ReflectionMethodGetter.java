package io.basc.framework.core.execution.reflect;

import java.lang.reflect.Method;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Getter;
import io.basc.framework.lang.ImpossibleException;
import io.basc.framework.util.reflect.ReflectionUtils;
import lombok.NonNull;

public class ReflectionMethodGetter extends ReflectionMethod implements Getter {
	private String name;

	public ReflectionMethodGetter(@NonNull Method method) {
		super(method);
	}

	@Override
	public String getName() {
		return name == null ? super.getName() : name;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return getReturnTypeDescriptor();
	}

	@Override
	public Object get(Object target) {
		try {
			return invoke(target);
		} catch (Throwable e) {
			ReflectionUtils.handleThrowable(e);
			throw new ImpossibleException();
		}
	}

	@Override
	public ReflectionMethodGetter rename(String name) {
		ReflectionMethodGetter getter = new ReflectionMethodGetter(getMember());
		getter.name = name;
		return getter;
	}

}
