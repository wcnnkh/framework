package io.basc.framework.execution.reflect;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.MethodExecutor;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReflectionMethodExecutor extends ReflectionMethod implements MethodExecutor {
	private Object target;

	public ReflectionMethodExecutor(Method executable, TypeDescriptor targetTypeDescriptor, Object target) {
		super(executable, targetTypeDescriptor);
		this.target = target;
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		return execute(target, args);
	}

	@Override
	public void setTarget(Object target) {
		this.target = target;
	}
}
