package run.soeasy.framework.core.execution.reflect;

import java.lang.reflect.Field;

import lombok.NonNull;
import run.soeasy.framework.core.execution.Setter;
import run.soeasy.framework.util.reflect.ReflectionUtils;

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
		ReflectionUtils.set(getSource(), target, value);
	}

	@Override
	public ReflectionFieldSetter rename(String name) {
		ReflectionFieldSetter setter = new ReflectionFieldSetter(getSource());
		setter.name = name;
		return setter;
	}

}
