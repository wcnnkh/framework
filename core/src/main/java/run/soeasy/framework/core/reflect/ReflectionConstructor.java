package run.soeasy.framework.core.reflect;

import java.lang.reflect.Constructor;

import lombok.NonNull;

public class ReflectionConstructor extends AbstractReflectionExecution<Constructor<?>> {

	public ReflectionConstructor(@NonNull Constructor<?> member) {
		super(member);
	}

	@Override
	public Object execute(@NonNull Object... args) throws Throwable {
		return ReflectionUtils.newInstance(getSource(), args);
	}

}
