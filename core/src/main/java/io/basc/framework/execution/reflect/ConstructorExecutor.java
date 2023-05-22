package io.basc.framework.execution.reflect;

import java.lang.reflect.Constructor;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.ExecutionException;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConstructorExecutor extends ExecutableExecutor<Constructor<?>> {
	public ConstructorExecutor(Constructor<?> constructor) {
		super(constructor);
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws ExecutionException {
		return ReflectionUtils.newInstance(getExecutable(), args.toArray());
	}

}