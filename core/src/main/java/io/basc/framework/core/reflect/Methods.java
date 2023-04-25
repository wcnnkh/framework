package io.basc.framework.core.reflect;

import java.lang.reflect.Method;
import java.util.function.Function;

import io.basc.framework.core.DefaultStructure;

public final class Methods extends MemberStructure<Method, Methods> {
	private final Function<? super MemberStructure<Method, Methods>, ? extends Methods> memberDecorator = (
			source) -> new Methods(source);

	public Methods(Class<?> source, Function<? super Class<?>, ? extends Method[]> processor) {
		super(source, processor);
	}

	private Methods(DefaultStructure<Method> members) {
		super(members);
	}

	@Override
	public Function<? super MemberStructure<Method, Methods>, ? extends Methods> getMemberStructureDecorator() {
		return memberDecorator;
	}

}
