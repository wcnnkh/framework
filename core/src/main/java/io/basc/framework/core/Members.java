package io.basc.framework.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.util.stream.StreamProcessorSupport;

/**
 * 类成员解析
 * 
 * @author wcnnkh
 *
 * @param <T>
 * @param <E>
 */
public class Members<T, E extends RuntimeException> implements Iterable<T>, Cloneable {
	@Nullable
	private List<Members<T, E>> withs;
	private final Class<?> sourceClass;
	private final Processor<Class<?>, Stream<T>, E> processor;

	public Members(Class<?> sourceClass, Processor<Class<?>, Stream<T>, E> processor) {
		this.sourceClass = sourceClass;
		this.processor = processor;
	}

	/**
	 * 过滤
	 * 
	 * @see Stream#filter(Predicate)
	 * @param predicate
	 * @return 返回一个新的methods
	 */
	public Members<T, E> filter(Predicate<? super T> predicate) {
		Members<T, E> methods = new Members<T, E>(sourceClass, (c) -> {
			Stream<T> stream = processor.process(c);
			if (stream == null) {
				return StreamProcessorSupport.emptyStream();
			}
			return stream.filter(predicate);
		});
		if (this.withs != null) {
			methods.withs = new ArrayList<>(this.withs);
		}
		return methods;
	}

	@Override
	public Members<T, E> clone() {
		Members<T, E> clone = new Members<T, E>(sourceClass, processor);
		if (this.withs != null) {
			clone.withs = new ArrayList<>(this.withs);
		}
		return clone;
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	@Override
	public Iterator<T> iterator() throws E {
		return stream().iterator();
	}

	/**
	 * 只获取当前的操作流
	 * 
	 * @return
	 */
	public Stream<T> stream() throws E {
		Stream<T> stream = processor.process(sourceClass);
		if (stream == null) {
			return StreamProcessorSupport.emptyStream();
		}
		return stream;
	}

	public Stream<Members<T, E>> streamMembers() throws E {
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
	public Stream<T> streamAll() throws E {
		return StreamProcessorSupport.concat(streamMembers().map((m) -> m.stream()).iterator());
	}

	public Members<T, E> with(Members<T, E> methods) {
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
	 * @param processor
	 * @return
	 */
	public Members<T, E> withClass(Class<?> sourceClass, Processor<Class<?>, Stream<T>, E> processor) {
		return with(new Members<>(sourceClass, processor));
	}

	/**
	 * 关联类
	 * 
	 * @param sourceClass
	 * @return
	 */
	public Members<T, E> withClass(Class<?> sourceClass) {
		return withClass(sourceClass, this.processor);
	}

	/**
	 * 该类上的所有接口(此方法不支持superclass的原因的，无法获取一个接口的父类，尝试获取一个接口的父类时始终为空)
	 * 
	 * @see Class#getInterfaces()
	 * @see #withInterfaces(Processor)
	 * @return
	 */
	public Members<T, E> withInterfaces() {
		return withInterfaces(this.processor);
	}

	/**
	 * 关联接口
	 * 
	 * @see Class#getInterfaces()
	 * 
	 * @param <E>
	 * @param processor
	 * @return
	 */
	public Members<T, E> withInterfaces(Processor<Class<?>, Stream<T>, E> processor) {
		Assert.requiredArgument(processor != null, "processor");
		Class<?>[] interfaces = sourceClass.getInterfaces();
		if (interfaces == null || interfaces.length == 0) {
			return this;
		}

		for (Class<?> interfaceClass : interfaces) {
			withClass(interfaceClass, processor);
		}
		return this;
	}

	/**
	 * 关联父类
	 * 
	 * @param <E>
	 * @param interfaces 是否也关联父类的接口
	 * @param processor
	 * @return
	 * @throws E
	 */
	public Members<T, E> withSuperclass(boolean interfaces, Processor<Class<?>, Stream<T>, E> processor) {
		Assert.requiredArgument(processor != null, "processor");
		Class<?> superclass = sourceClass.getSuperclass();
		while (superclass != null) {
			if (interfaces) {
				withClass(superclass, processor).withInterfaces(processor);
			} else {
				withClass(superclass, processor);
			}
			superclass = superclass.getSuperclass();
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
	public Members<T, E> withSuperclass(Processor<Class<?>, Stream<T>, E> processor) {
		return withSuperclass(false, processor);
	}

	/**
	 * 关联所有父类
	 * 
	 * @param interfaces 是否关联父类的接口 {@link #withInterfaces(boolean)}
	 * @return
	 */
	public Members<T, E> withSuperclass(boolean interfaces) {
		return withSuperclass(interfaces, this.processor);
	}

	/**
	 * 关联所有父类
	 * 
	 * @return
	 */
	public Members<T, E> withSuperclass() {
		return withSuperclass(false);
	}

	/**
	 * 关联所有的接口和父类
	 * 
	 * @param <E>
	 * @param processor
	 * @return
	 * @throws E
	 */
	public Members<T, E> withAll(Processor<Class<?>, Stream<T>, E> processor) {
		return withInterfaces(processor).withSuperclass(processor);
	}

	/**
	 * 关联所有
	 * 
	 * @return
	 */
	public Members<T, E> withAll() {
		return withAll(this.processor);
	}
}
