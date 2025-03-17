package run.soeasy.framework.core.execution.reflect;

import java.lang.reflect.Member;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execution.Executable;

@Data
public abstract class ReflectionMember<T extends Member> implements Executable {
	@NonNull
	private final T member;

	@Override
	public String getName() {
		return getMember().getName();
	}

	@Override
	public int getModifiers() {
		return getMember().getModifiers();
	}

	public Class<?> getDeclaringClass() {
		return getMember().getDeclaringClass();
	}

	@Override
	public TypeDescriptor getDeclaringTypeDescriptor() {
		return TypeDescriptor.valueOf(getDeclaringClass());
	}

}
