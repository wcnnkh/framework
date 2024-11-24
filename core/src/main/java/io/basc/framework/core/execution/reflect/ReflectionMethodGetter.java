package io.basc.framework.core.execution.reflect;

import java.lang.reflect.Method;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Getter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.reflect.ReflectionUtils;

public class ReflectionMethodGetter extends ReflectionMethod implements Getter {
	private String name;

	public ReflectionMethodGetter(Method executable) {
		super(executable);
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
			return execute(target);
		} catch (Throwable e) {
			ReflectionUtils.handleThrowable(e);
			throw Assert.shouldNeverGetHere();
		}
	}

	@Override
	public ReflectionMethodGetter rename(String name) {
		ReflectionMethodGetter getter = new ReflectionMethodGetter(getMember());
		getter.name = name;
		return getter;
	}

}
