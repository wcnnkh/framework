package io.basc.framework.execution.reflect;

import java.lang.reflect.Member;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executable;
import lombok.Data;
import lombok.NonNull;

@Data
public abstract class ReflectionMember<T extends Member> implements Executable {
	@NonNull
	private final T member;
	
	public final T getMember() {
		return member;
	}

	@Override
	public String getName() {
		return member.getName();
	}

	@Override
	public int getModifiers() {
		return member.getModifiers();
	}

	@Override
	public TypeDescriptor getDeclaringTypeDescriptor() {
		return TypeDescriptor.valueOf(member.getDeclaringClass());
	}
	
}
