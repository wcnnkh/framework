package run.soeasy.framework.core.reflect;

import java.lang.reflect.Method;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReflectionMethod extends ReflectionExecutable<Method> implements run.soeasy.framework.core.execution.Method {
	private Object target;

	public ReflectionMethod(@NonNull Method method) {
		super(method);
	}

	@Override
	public Object invoke(Object target, @NonNull Object... args) throws Throwable {
		return ReflectionUtils.invoke(getSource(), target, args);
	}

	@Override
	public final Object execute(@NonNull Object... args) throws Throwable {
		return invoke(getTarget(), args);
	}
}
