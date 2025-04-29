package run.soeasy.framework.core.reflect;

import java.lang.reflect.Method;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import run.soeasy.framework.core.invoke.Invocation;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReflectionMethod extends AbstractReflectionExecution<Method> implements Invocation {
	private Object target;

	public ReflectionMethod(@NonNull Method method) {
		super(method);
	}

	@Override
	public Object invoke(Object target, @NonNull Object... args) {
		return ReflectionUtils.invoke(getSource(), target, args);
	}
}
