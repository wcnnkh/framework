package io.basc.framework.context.support;

import java.util.LinkedHashSet;
import java.util.stream.Stream;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.util.Cursor;

public class LinkedHashSetClassesLoader extends LinkedHashSet<Class<?>> implements ClassesLoader {
	private static final long serialVersionUID = 1L;

	@Override
	public Stream<Class<?>> stream() {
		return super.stream();
	}

	@Override
	public Cursor<Class<?>> iterator() {
		return Cursor.create(super.iterator());
	}

	@Override
	public void reload() {
	}

}
