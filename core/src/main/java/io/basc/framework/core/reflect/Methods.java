package io.basc.framework.core.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.util.stream.StreamProcessorSupport;

public class Methods implements Iterable<Method>, Cloneable {
	private final LinkedList<Methods> list = new LinkedList<>();
	private final Class<?> sourceClass;
	private final Supplier<Stream<Method>> supplier;

	private Methods(Class<?> sourceClass, Supplier<Stream<Method>> supplier) {
		this.sourceClass = sourceClass;
		this.supplier = supplier;
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	@Override
	public Iterator<Method> iterator() {
		return stream().iterator();
	}

	public Stream<Method> stream() {
		return supplier.get();
	}

	public Methods addFirst(Methods methods) {
		methods.addFirst(methods);
		return this;
	}

	public Methods addLast(Methods methods) {
		methods.addLast(methods);
		return this;
	}

	public Stream<Methods> streamMethods() {
		return Stream.concat(Stream.of(this), list.stream());
	}

	public Stream<Method> streamAll() {
		return StreamProcessorSupport.concat(streamMethods().map((m) -> m.stream()).iterator());
	}

	public Methods andMethodsOnSuperclass() {
		Class<?> superclass = sourceClass.getSuperclass();
		if (superclass == null) {
			return this;
		}
		return andMethods(superclass);
	}

	public Methods andDeclaredMethodsOnSuperclass() {
		Class<?> superclass = sourceClass.getSuperclass();
		if (superclass == null) {
			return this;
		}
		return andMethods(superclass);
	}

	public Methods andMethods(Class<?> sourceClass) {
		return addLast(getMethods(sourceClass));
	}

	public Methods andDeclaredMethods(Class<?> sourceClass) {
		return addLast(getDeclaredMethods(sourceClass));
	}

	public Methods andMethodsOnInterfaces(boolean declared) {
		Class<?>[] interfaceClasses = sourceClass.getInterfaces();
		if (interfaceClasses == null || interfaceClasses.length == 0) {
			return this;
		}

		return new Methods(sourceClass, () -> Stream.concat(stream(), StreamProcessorSupport.concat(
				Arrays.asList(interfaceClasses).stream().map((c) -> getMethods(c, declared).stream()).iterator())));
	}

	public Methods andMethodsOnInterfaces() {
		return andMethodsOnInterfaces(false);
	}

	public Methods andDeclaredMethodsOnInterfaces() {
		return andMethodsOnInterfaces(true);
	}

	private static List<Method> getMethods(Class<?> sourceClass, boolean declared) {
		Method[] methods = declared ? sourceClass.getDeclaredMethods() : sourceClass.getMethods();
		return methods == null ? Collections.emptyList() : Arrays.asList(methods);
	}

	public static Methods getMethods(Class<?> sourceClass) {
		return new Methods(sourceClass, () -> getMethods(sourceClass, false).stream());
	}

	public static Methods getDeclaredMethods(Class<?> sourceClass) {
		return new Methods(sourceClass, () -> getMethods(sourceClass, true).stream());
	}
}
