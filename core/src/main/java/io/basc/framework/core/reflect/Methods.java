package io.basc.framework.core.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.util.stream.StreamProcessorSupport;

public class Methods implements Iterable<Method>, Cloneable {
	private final Class<?> sourceClass;
	private final Supplier<Stream<Method>> supplier;
	private final LinkedList<Methods> withs = new LinkedList<Methods>();

	private Methods(Class<?> sourceClass, Supplier<Stream<Method>> supplier) {
		this.sourceClass = sourceClass;
		this.supplier = supplier;
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	@Override
	public Methods clone() {
		Methods clone = new Methods(sourceClass, supplier);
		if (!this.withs.isEmpty()) {
			clone.withs.addAll(this.withs);
		}
		return clone;
	}

	public List<Methods> getWiths() {
		return withs;
	}

	@Override
	public Iterator<Method> iterator() {
		return stream().iterator();
	}

	public Stream<Method> stream() {
		return Stream.concat(supplier.get(),
				StreamProcessorSupport.concat(withs.stream().map((m) -> m.stream()).iterator()));
	}

	public Stream<Methods> streamMethods() {
		return Stream.concat(Stream.of(this), withs.stream());
	}

	public Stream<Method> streamAll() {
		return StreamProcessorSupport.concat(streamMethods().map((m) -> m.stream()).iterator());
	}

	public Methods with(Methods methods) {
		withs.add(methods);
		return this;
	}

	public Methods withClass(Class<?> sourceClass, boolean declared) {
		return with(with(forClass(sourceClass, declared)));
	}

	public Methods withInterfaces(boolean declared) {
		Class<?>[] interfaces = sourceClass.getInterfaces();
		if (interfaces == null || interfaces.length == 0) {
			return this;
		}

		for (Class<?> interfaceClass : interfaces) {
			withs.add(forClass(interfaceClass, declared));
		}
		return this;
	}

	public Methods withSuperclass(boolean declared, boolean interfaces) {
		Class<?> superclass = sourceClass.getSuperclass();
		while (superclass != null) {
			withs.add(interfaces ? forClass(superclass, declared).withInterfaces(declared)
					: forClass(superclass, declared));
		}
		return this;
	}

	public static Methods forClass(Class<?> sourceClass, boolean declared) {
		return new Methods(sourceClass, () -> {
			Method[] methods = declared ? sourceClass.getDeclaredMethods() : sourceClass.getMethods();
			if (methods == null) {
				return StreamProcessorSupport.emptyStream();
			}
			return Arrays.asList(methods).stream();
		});
	}
}
