package io.basc.framework.core.execution.reflect;

import java.lang.reflect.Field;

import io.basc.framework.core.execution.Setter;
import io.basc.framework.util.reflect.ReflectionUtils;
import lombok.NonNull;

public class ReflectionFieldSetter extends ReflectionField implements Setter {
	private static final long serialVersionUID = 1L;
	private String name;

	public ReflectionFieldSetter(@NonNull Field member) {
		super(member);
	}

	@Override
	public String getName() {
		return name == null ? super.getName() : name;
	}

	@Override
	public void set(Object target, Object value) {
		ReflectionUtils.set(getMember(), target, value);
	}

	@Override
	public ReflectionFieldSetter rename(String name) {
		ReflectionFieldSetter setter = new ReflectionFieldSetter(getMember());
		setter.name = name;
		return setter;
	}

}
