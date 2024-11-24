package io.basc.framework.core.execution.reflect;

import java.lang.reflect.Member;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Executable;
import lombok.Data;
import lombok.NonNull;

@Data
public abstract class ReflectionMember<T extends Member> implements Executable {
	@NonNull
	private volatile T member;

	public T getMember() {
		return member;
	}

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
