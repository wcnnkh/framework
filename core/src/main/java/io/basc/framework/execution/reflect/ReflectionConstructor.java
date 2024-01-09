package io.basc.framework.execution.reflect;

import java.lang.reflect.Constructor;

import io.basc.framework.execution.Function;
import io.basc.framework.util.element.Elements;
import lombok.NonNull;

public class ReflectionConstructor extends ReflectionExecutable<Constructor<?>> implements Function {
	public ReflectionConstructor(@NonNull Constructor<?> member) {
		super(member);
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		return getMember().newInstance(args);
	}

}
