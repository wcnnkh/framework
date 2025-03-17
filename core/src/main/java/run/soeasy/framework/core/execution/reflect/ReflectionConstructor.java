package run.soeasy.framework.core.execution.reflect;

import java.lang.reflect.Constructor;

import lombok.NonNull;
import run.soeasy.framework.core.execution.Function;

public class ReflectionConstructor extends ReflectionExecutable<Constructor<?>> implements Function {
	public ReflectionConstructor(@NonNull Constructor<?> member) {
		super(member);
	}

	@Override
	public Object execute(@NonNull Object... args) throws Throwable {
		return getMember().newInstance(args);
	}

}
