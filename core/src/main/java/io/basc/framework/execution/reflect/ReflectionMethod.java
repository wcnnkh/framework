package io.basc.framework.execution.reflect;

import java.lang.reflect.Method;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReflectionMethod extends ReflectionExecutable<Method> implements io.basc.framework.execution.Method {
	private Object target;

	public ReflectionMethod(Method executable) {
		super(executable);
	}

	@Override
	public Object execute(Object target, Elements<? extends Object> args) throws Throwable {
		return ReflectionUtils.invoke(getMember(), target, args.toArray());
	}

	@Override
	public final Object execute(Elements<? extends Object> args) throws Throwable {
		return execute(getTarget(), args);
	}
}
