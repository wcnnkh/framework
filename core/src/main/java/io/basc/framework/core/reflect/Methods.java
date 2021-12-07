package io.basc.framework.core.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.util.stream.StreamProcessorSupport;

public class Methods implements Iterable<Method>, Cloneable {
	@Nullable
	private List<Methods> withs;
	private final Class<?> sourceClass;
	private final Supplier<Stream<Method>> supplier;

	public Methods(Class<?> sourceClass, Supplier<Stream<Method>> supplier) {
		this.sourceClass = sourceClass;
		this.supplier = supplier;
	}

	/**
	 * 过滤
	 * 
	 * @see Stream#filter(Predicate)
	 * @param predicate
	 * @return 返回一个新的methods
	 */
	public Methods filter(Predicate<? super Method> predicate) {
		Methods methods = new Methods(sourceClass, () -> supplier.get().filter(predicate));
		if (this.withs != null) {
			methods.withs = new ArrayList<>(this.withs);
		}
		return methods;
	}

	@Override
	public Methods clone() {
		Methods clone = new Methods(sourceClass, supplier);
		if (this.withs != null) {
			clone.withs = new ArrayList<>(this.withs);
		}
		return clone;
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	@Override
	public Iterator<Method> iterator() {
		return stream().iterator();
	}

	/**
	 * 只获取当前的操作流
	 * 
	 * @return
	 */
	public Stream<Method> stream() {
		return supplier.get();
	}

	public Stream<Methods> streamMethods() {
		if (withs == null) {
			return Stream.of(this);
		}
		return Stream.concat(Stream.of(this), withs.stream());
	}

	/**
	 * 合并全部后的操作流
	 * 
	 * @return
	 */
	public Stream<Method> streamAll() {
		return StreamProcessorSupport.concat(streamMethods().map((m) -> m.stream()).iterator());
	}

	public Methods with(Methods methods) {
		if (withs == null) {
			withs = new LinkedList<>();
		}
		withs.add(methods);
		return this;
	}

	/**
	 * 关联类
	 * 
	 * @param sourceClass
	 * @param declared
	 * @return
	 */
	public Methods withClass(Class<?> sourceClass, boolean declared) {
		return with(with(forClass(sourceClass, declared)));
	}

	/**
	 * 该类上的所有接口(此方法不支持superclass的原因的，无法获取一个接口的父类，尝试获取一个接口的父类时始终为空)
	 * 
	 * @see Class#getInterfaces()
	 * @see #withInterfaces(Processor)
	 * @param declared
	 * @return
	 */
	public Methods withInterfaces(boolean declared) {
		return withInterfaces((c) -> forClass(c, declared));
	}

	/**
	 * 关联接口
	 * 
	 * @see Class#getInterfaces()
	 * 
	 * @param <E>
	 * @param processor
	 * @return
	 * @throws E
	 */
	public <E extends Throwable> Methods withInterfaces(Processor<Class<?>, Methods, E> processor) throws E {
		Assert.requiredArgument(processor != null, "processor");
		Class<?>[] interfaces = sourceClass.getInterfaces();
		if (interfaces == null || interfaces.length == 0) {
			return this;
		}

		for (Class<?> interfaceClass : interfaces) {
			Methods methods = processor.process(interfaceClass);
			if (methods == null) {
				continue;
			}
			with(methods);
		}
		return this;
	}

	/**
	 * 关联父类
	 * 
	 * @param <E>
	 * @param processor
	 * @return
	 * @throws E
	 */
	public <E extends Throwable> Methods withSuperclass(Processor<Class<?>, Methods, E> processor) throws E {
		Assert.requiredArgument(processor != null, "processor");
		Class<?> superclass = sourceClass.getSuperclass();
		while (superclass != null) {
			Methods methods = processor.process(superclass);
			if (methods == null) {
				continue;
			}
			with(methods);
			superclass = superclass.getSuperclass();
		}
		return this;
	}

	/**
	 * 关联所有父类
	 * 
	 * @param declared
	 * @param interfaces 是否关联父类的接口 {@link #withInterfaces(boolean)}
	 * @return
	 */
	public Methods withSuperclass(boolean declared, boolean interfaces) {
		return withSuperclass((superclass) -> interfaces ? forClass(superclass, declared).withInterfaces(declared)
				: forClass(superclass, declared));
	}

	/**
	 * 关联所有的接口和父类
	 * 
	 * @param <E>
	 * @param processor
	 * @return
	 * @throws E
	 */
	public <E extends Throwable> Methods withAll(Processor<Class<?>, Methods, E> processor) throws E {
		return withInterfaces(processor).withSuperclass(processor);
	}

	/**
	 * 关联所有
	 * 
	 * @param declared
	 * @return
	 */
	public Methods withAll(boolean declared) {
		return withInterfaces(declared).withSuperclass(declared, true);
	}

	public static Methods forClass(Class<?> sourceClass, boolean declared) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		return new Methods(sourceClass, () -> {
			Method[] methods = declared ? sourceClass.getDeclaredMethods() : sourceClass.getMethods();
			if (methods == null) {
				return StreamProcessorSupport.emptyStream();
			}
			return Arrays.asList(methods).stream();
		});
	}
}
