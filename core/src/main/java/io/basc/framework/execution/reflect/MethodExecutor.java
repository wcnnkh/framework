package io.basc.framework.execution.reflect;

import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.aop.SwitchableTargetExecutor;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodExecutor extends ExecutableExecutor<Method> implements SwitchableTargetExecutor {
	private Object target;

	public MethodExecutor(TypeDescriptor source, Method executable, Object target) {
		super(source, executable);
		this.target = target;
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		return ReflectionUtils.invoke(getExecutable(), target, args.toArray());
	}

	@Override
	public void setTarget(Object target) {
		this.target = target;
	}
}
