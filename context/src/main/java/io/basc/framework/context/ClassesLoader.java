package io.basc.framework.context;

import java.util.function.Predicate;

import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.util.Cursor;

public interface ClassesLoader extends ServiceLoader<Class<?>> {
	static final String SUFFIX = ".class";

	@Override
	default ClassesLoader filter(Predicate<? super Class<?>> predicate) {
		return new ClassesLoader() {

			@Override
			public void reload() {
				ClassesLoader.this.reload();
			}

			@Override
			public Cursor<Class<?>> iterator() {
				return ClassesLoader.this.iterator().filter(predicate);
			}
		};
	}

	default ClassesLoader distinct() {
		ServiceLoader<Class<?>> serviceLoader = flatConvert((e) -> e.distinct());
		return new ClassesLoader() {
			@Override
			public Cursor<Class<?>> iterator() {
				return serviceLoader.iterator();
			}

			@Override
			public void reload() {
				serviceLoader.reload();
			}
		};
	}
}
