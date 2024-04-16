package io.basc.framework.execution.reflect;

import java.lang.reflect.Field;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Getter;
import lombok.NonNull;

public class ReflectionFieldGetter extends ReflectionField implements Getter {
	private static final long serialVersionUID = 1L;
	private String name;

	public ReflectionFieldGetter(@NonNull Field member) {
		super(member);
	}

	@Override
	public Object get(Object target) {
		return ReflectionUtils.get(getMember(), target);
	}

	@Override
	public String getName() {
		return name == null ? super.getName() : name;
	}

	@Override
	public ReflectionFieldGetter rename(String name) {
		ReflectionFieldGetter fieldGetter = new ReflectionFieldGetter(getMember());
		fieldGetter.name = name;
		return fieldGetter;
	}
}
