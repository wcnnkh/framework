package io.basc.framework.execution.reflect;

import java.lang.reflect.Method;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodExecutor extends ExecutableExecutor<Method> {
	private final Object target;

	public MethodExecutor(Object target, Method method) {
		super(method);
		this.target = target;
	}

	@Override
	public Object execute(Elements<? extends Object> args) {
		return ReflectionUtils.invoke(getExecutable(), target, args.toArray());
	}
}
