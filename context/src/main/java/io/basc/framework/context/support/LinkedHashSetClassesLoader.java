package io.basc.framework.context.support;

import java.util.LinkedHashSet;
import java.util.stream.Stream;

import io.basc.framework.context.ClassesLoader;

public class LinkedHashSetClassesLoader extends LinkedHashSet<Class<?>> implements ClassesLoader {
	private static final long serialVersionUID = 1L;

	@Override
	public Stream<Class<?>> stream() {
		return super.stream();
	}

	@Override
	public void reload() {
	}

}
