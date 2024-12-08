package io.basc.framework.core.execution.reflect;

import java.lang.reflect.Constructor;

import io.basc.framework.core.execution.Function;
import lombok.NonNull;

public class ReflectionConstructor extends ReflectionExecutable<Constructor<?>> implements Function {
	public ReflectionConstructor(@NonNull Constructor<?> member) {
		super(member);
	}

	@Override
	public Object execute(@NonNull Object... args) throws Throwable {
		return getMember().newInstance(args);
	}

}
