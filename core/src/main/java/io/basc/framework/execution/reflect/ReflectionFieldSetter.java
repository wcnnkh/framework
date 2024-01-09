package io.basc.framework.execution.reflect;

import java.lang.reflect.Field;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Setter;
import lombok.NonNull;

public class ReflectionFieldSetter extends ReflectionField implements Setter {
	private String name;

	public ReflectionFieldSetter(@NonNull Field member) {
		super(member);
	}

	@Override
	public String getName() {
		return name == null ? super.getName() : name;
	}

	@Override
	public void set(Object target, Object value) throws Throwable {
		ReflectionUtils.set(getMember(), target, value);
	}

	@Override
	public ReflectionFieldSetter rename(String name) {
		ReflectionFieldSetter setter = new ReflectionFieldSetter(getMember());
		setter.name = name;
		return setter;
	}

}
