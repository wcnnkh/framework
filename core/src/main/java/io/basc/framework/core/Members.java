package io.basc.framework.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.util.stream.StreamProcessorSupport;

/**
 * 类成员
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public class Members<T> implements Iterable<T>, Cloneable, Supplier<T> {
	private final Class<?> sourceClass;
	private final Processor<Class<?>, Stream<T>, ? extends RuntimeException> processor;
	@Nullable
	private List<Members<T>> withs;

	public Members(Class<?> sourceClass, Processor<Class<?>, Stream<T>, ? extends RuntimeException> processor) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(processor != null, "processor");
		this.sourceClass = sourceClass;
		this.processor = processor;
	}

	public List<Members<T>> getWiths() {
		return this.withs == null ? Collections.emptyList() : Collections.unmodifiableList(this.withs);
	}

	/**
	 * 过滤
	 * 
	 * @see #map(Processor)
	 * @see Stream#filter(Predicate)
	 * @param predicate
	 * @return 返回一个新的
	 */
	public Members<T> filter(Predicate<? super T> predicate) {
		return map((s) -> s.filter(predicate));
	}

	/**
	 * 映射
	 * 
	 * @param processor
	 * @return 返回一个新的
	 */
	public <S> Members<S> map(Processor<Stream<T>, Stream<S>, ? extends RuntimeException> processor) {
		Members<S> members = new Members<S>(this.sourceClass, (c) -> {
			Stream<T> stream = Members.this.stream();
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
			members.withs = new LinkedList<Members<S>>();
			for (Members<T> source : this.withs) {
				if (source == this) {
					members.withs.add(members);
				} else {
					members.withs.add(source.map(processor));
				}
			}
		}
		return members;
	}

	@Override
	public Members<T> clone() {
		Members<T> clone = new Members<T>(this.sourceClass, this.processor);
		if (this.withs != null) {
			clone.withs = new LinkedList<>(this.withs);
		}
		return clone;
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	/**
	 * @see #stream()
	 */
	@Override
	public Iterator<T> iterator() {
		return stream().iterator();
	}

	/**
	 * 只获取当前的操作流
	 * 
	 * @return
	 */
	public Stream<T> stream() {
		Stream<T> stream = this.processor.process(sourceClass);
		if (stream == null) {
			return StreamProcessorSupport.emptyStream();
		}
		return stream;
	}

	public Stream<Members<T>> streamMembers() {
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
	public Stream<T> streamAll() {
		return StreamProcessorSupport.concat(streamMembers().map((m) -> m.stream()).iterator());
	}

	public Members<T> with(Members<T> methods) {
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
	public Members<T> withClass(Class<?> sourceClass,
			Processor<Class<?>, Stream<T>, ? extends RuntimeException> processor) {
		return with(new Members<>(sourceClass, processor));
	}

	/**
	 * 关联类
	 * 
	 * @param sourceClass
	 * @return
	 */
	public Members<T> withClass(Class<?> sourceClass) {
		return withClass(sourceClass, this.processor);
	}

	/**
	 * 关联接口
	 * 
	 * @param predicate
	 * @param processor
	 * @return
	 */
	public Members<T> withInterfaces(@Nullable Predicate<Class<?>> predicate,
			Processor<Class<?>, Stream<T>, ? extends RuntimeException> processor) {
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
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<T> withInterfaces(@Nullable Predicate<Class<?>> predicate) {
		return withInterfaces(predicate, this.processor);
	}

	/**
	 * 该类上的所有接口(此方法不支持superclass的原因的，无法获取一个接口的父类，尝试获取一个接口的父类时始终为空)
	 * 
	 * @see Class#getInterfaces()
	 * @see #withInterfaces(Processor)
	 * @return
	 */
	public Members<T> withInterfaces() {
		return withInterfaces(null, this.processor);
	}

	/**
	 * 关联所有父类
	 * 
	 * @param interfaces 是否关联父类的接口
	 * @param predicate
	 * @param processor
	 * @return
	 */
	public Members<T> withSuperclass(boolean interfaces, @Nullable Predicate<Class<?>> predicate,
			Processor<Class<?>, Stream<T>, ? extends RuntimeException> processor) {
		Assert.requiredArgument(processor != null, "processor");
		Class<?> superclass = this.sourceClass.getSuperclass();
		while (superclass != null) {
			if (predicate == null || predicate.test(superclass)) {
				if (interfaces) {
					withClass(superclass, processor).withInterfaces(predicate, processor);
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
	 * @param interfaces 是否也关联父类的接口
	 * @param predicate
	 * @return
	 */
	public Members<T> withSuperclass(boolean interfaces, @Nullable Predicate<Class<?>> predicate) {
		return withSuperclass(interfaces, predicate, this.processor);
	}

	public Members<T> withSuperclass(@Nullable Predicate<Class<?>> predicate,
			Processor<Class<?>, Stream<T>, ? extends RuntimeException> processor) {
		return withSuperclass(false, predicate, processor);
	}

	/**
	 * 关联父类(不关联父类接口)
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<T> withSuperclass(@Nullable Predicate<Class<?>> predicate) {
		return withSuperclass(false, predicate);
	}

	/**
	 * 关联所有父类
	 * 
	 * @param interfaces 是否关联父类的接口 {@link #withInterfaces(boolean)}
	 * @return
	 */
	public Members<T> withSuperclass(boolean interfaces) {
		return withSuperclass(interfaces, null, this.processor);
	}

	/**
	 * 关联所有父类(不关联父类接口)
	 * 
	 * @return
	 */
	public Members<T> withSuperclass() {
		return withSuperclass(false);
	}

	public Members<T> withAll(@Nullable Predicate<Class<?>> predicate,
			Processor<Class<?>, Stream<T>, ? extends RuntimeException> processor) {
		return withInterfaces(predicate, processor).withSuperclass(predicate, processor);
	}

	/**
	 * 关联所有的接口和父类(不关联父类接口)
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<T> withAll(@Nullable Predicate<Class<?>> predicate) {
		return withInterfaces(predicate).withSuperclass(predicate);
	}

	/**
	 * 关联所有(不关联父类接口)
	 * 
	 * @return
	 */
	public Members<T> withAll() {
		return withAll(null, this.processor);
	}

	/**
	 * @see #streamAll()
	 * @see Stream#findFirst()
	 * @return
	 */
	public Optional<T> findFirst() {
		return streamAll().findFirst();
	}

	/**
	 * @see #streamAll()
	 * @see Stream#findAny()
	 * @return
	 * @throws E
	 */
	public Optional<T> findAny() {
		return streamAll().findAny();
	}

	/**
	 * 获取第一个
	 * 
	 * @see #findFirst()
	 */
	@Nullable
	@Override
	public T get() {
		return findFirst().orElse(null);
	}
}
