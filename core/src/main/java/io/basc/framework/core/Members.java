package io.basc.framework.core;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.util.stream.StreamProcessorSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 类成员解析
 * 
 * @author wcnnkh
 *
 * @param <T>
 * @param <E>
 */
public class Members<T, E extends RuntimeException> implements Iterable<T>,
		Cloneable {
	@Nullable
	private List<Members<T, E>> withs;
	private final Class<?> sourceClass;
	private final Processor<Class<?>, Stream<T>, E> processor;

	public Members(Class<?> sourceClass,
			Processor<Class<?>, Stream<T>, E> processor) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(processor != null, "processor");
		this.sourceClass = sourceClass;
		this.processor = processor;
	}

	public List<Members<T, E>> getWiths() {
		return Collections.unmodifiableList(withs);
	}

	public Processor<Class<?>, Stream<T>, E> getProcessor() {
		return processor;
	}

	/**
	 * 过滤
	 * 
	 * @see #map(Processor)
	 * @see Stream#filter(Predicate)
	 * @param predicate
	 * @return 返回一个新的methods
	 */
	public Members<T, E> filter(Predicate<? super T> predicate) {
		return map((s) -> s.filter(predicate));
	}

	/**
	 * 映射
	 * 
	 * @param processor
	 * @return
	 */
	public <S> Members<S, E> map(Processor<Stream<T>, Stream<S>, E> processor) {
		Members<S, E> members = new Members<S, E>(sourceClass, (c) -> {
			Stream<T> stream = this.processor.process(c);
			if (stream == null) {
				return StreamProcessorSupport.emptyStream();
			}
			Stream<S> target = processor.process(stream);
			if (target == null) {
				return StreamProcessorSupport.emptyStream();
			}
			return target;
		});
		if (this.withs != null) {
			members.withs = new ArrayList<Members<S, E>>();
			for (Members<T, E> source : this.withs) {
				members.withs.add(source.map(processor));
			}
		}
		return members;
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
		return StreamProcessorSupport.concat(streamMembers().map(
				(m) -> m.stream()).iterator());
	}

	public Members<T, E> with(Members<T, E> methods) {
		if (this.withs == null) {
			this.withs = new LinkedList<>();
		}
		this.withs.add(methods);
		return this;
	}

	/**
	 * 关联类
	 * 
	 * @param sourceClass
	 * @param processor
	 * @return
	 */
	public Members<T, E> withClass(Class<?> sourceClass,
			Processor<Class<?>, Stream<T>, E> processor) {
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
	 * 关联接口
	 * @param predicate
	 * @param processor
	 * @return
	 */
	public Members<T, E> withInterfaces(
			@Nullable Predicate<Class<?>> predicate,
			Processor<Class<?>, Stream<T>, E> processor) {
		Assert.requiredArgument(processor != null, "processor");
		Class<?>[] interfaces = this.sourceClass.getInterfaces();
		if (interfaces == null || interfaces.length == 0) {
			return this;
		}

		for (Class<?> interfaceClass : interfaces) {
			if (predicate == null || predicate.test(interfaceClass)) {
				withClass(interfaceClass, processor);
			} else {
				break;
			}
		}
		return this;
	}

	/**
	 * 关联接口
	 * @param predicate
	 * @return
	 */
	public Members<T, E> withInterfaces(@Nullable Predicate<Class<?>> predicate) {
		return withInterfaces(predicate, this.processor);
	}

	/**
	 * 该类上的所有接口(此方法不支持superclass的原因的，无法获取一个接口的父类，尝试获取一个接口的父类时始终为空)
	 * 
	 * @see Class#getInterfaces()
	 * @see #withInterfaces(Processor)
	 * @return
	 */
	public Members<T, E> withInterfaces() {
		return withInterfaces(null, this.processor);
	}

	/**
	 * 关联所有父类
	 * @param interfaces 是否关联父类的接口
	 * @param predicate
	 * @param processor
	 * @return
	 */
	public Members<T, E> withSuperclass(boolean interfaces,
			@Nullable Predicate<Class<?>> predicate,
			Processor<Class<?>, Stream<T>, E> processor) {
		Assert.requiredArgument(processor != null, "processor");
		Class<?> superclass = this.sourceClass.getSuperclass();
		while (superclass != null) {
			if (predicate == null || predicate.test(superclass)) {
				if (interfaces) {
					withClass(superclass, processor).withInterfaces(predicate,
							processor);
				} else {
					withClass(superclass, processor);
				}
			} else {
				break;
			}
			superclass = superclass.getSuperclass();
		}
		return this;
	}

	/**
	 * 关联父类
	 * 
	 * @param interfaces
	 *            是否也关联父类的接口
	 * @param predicate
	 * @return
	 */
	public Members<T, E> withSuperclass(boolean interfaces,
			@Nullable Predicate<Class<?>> predicate) {
		return withSuperclass(interfaces, predicate, this.processor);
	}
	
	public Members<T, E> withSuperclass(@Nullable Predicate<Class<?>> predicate, Processor<Class<?>, Stream<T>, E> processor) {
		return withSuperclass(false, predicate, processor);
	}

	/**
	 * 关联父类(不关联父类接口)
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<T, E> withSuperclass(@Nullable Predicate<Class<?>> predicate) {
		return withSuperclass(false, predicate);
	}

	/**
	 * 关联所有父类
	 * 
	 * @param interfaces
	 *            是否关联父类的接口 {@link #withInterfaces(boolean)}
	 * @return
	 */
	public Members<T, E> withSuperclass(boolean interfaces) {
		return withSuperclass(interfaces, null, this.processor);
	}

	/**
	 * 关联所有父类(不关联父类接口)
	 * 
	 * @return
	 */
	public Members<T, E> withSuperclass() {
		return withSuperclass(false);
	}
	
	public Members<T, E> withAll(@Nullable Predicate<Class<?>> predicate, Processor<Class<?>, Stream<T>, E> processor) {
		return withInterfaces(predicate, processor).withSuperclass(predicate, processor);
	}

	/**
	 * 关联所有的接口和父类(不关联父类接口)
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<T, E> withAll(@Nullable Predicate<Class<?>> predicate) {
		return withInterfaces(predicate).withSuperclass(predicate);
	}

	/**
	 * 关联所有(不关联父类接口)
	 * 
	 * @return
	 */
	public Members<T, E> withAll() {
		return withAll(null, this.processor);
	}
}
