package run.soeasy.framework.core.execution.reflect;

import java.lang.reflect.Member;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execution.Executable;
import run.soeasy.framework.util.function.Wrapper;

@Data
public abstract class ReflectionMember<T extends Member> implements Executable, Wrapper<T> {
	@NonNull
	private final T source;

	@Override
	public String getName() {
		return getSource().getName();
	}

	@Override
	public int getModifiers() {
		return getSource().getModifiers();
	}

	public Class<?> getDeclaringClass() {
		return getSource().getDeclaringClass();
	}

	@Override
	public TypeDescriptor getDeclaringTypeDescriptor() {
		return TypeDescriptor.valueOf(getDeclaringClass());
	}

}
