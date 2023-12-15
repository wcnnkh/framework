package io.basc.framework.execution.reflect;

import java.lang.reflect.Constructor;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReflectionConstructor extends ReflectionExecutor<Constructor<?>> {
	private Class<?>[] aopInterfaces;

	public ReflectionConstructor(Constructor<?> target) {
		super(target);
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		return ReflectionUtils.newInstance(getExecutable(), args);
	}

}
