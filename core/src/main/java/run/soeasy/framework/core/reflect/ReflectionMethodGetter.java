package run.soeasy.framework.core.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.invoke.mapping.Getter;

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
			throw new UndeclaredThrowableException(e);
		}
	}

	@Override
	public ReflectionMethodGetter rename(String name) {
		ReflectionMethodGetter getter = new ReflectionMethodGetter(getSource());
		getter.name = name;
		return getter;
	}

}
