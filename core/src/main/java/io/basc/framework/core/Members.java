package io.basc.framework.core;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.page.Pageables;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.util.stream.StreamProcessorSupport;

/**
 * 类成员
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public class Members<T> implements Cloneable, Supplier<T>, Pageables<Class<?>, T> {
	private final Function<Class<?>, ? extends Stream<T>> processor;
	@Nullable
	private List<T> shared;
	private final Class<?> sourceClass;
	@Nullable
	private Stream<T> stream;

	@Nullable
	private Members<T> with;

	public Members(Class<?> sourceClass, Function<Class<?>, ? extends Stream<T>> processor) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(processor != null, "processor");
		this.sourceClass = sourceClass;
		this.processor = processor;
	}

	public Members(Members<T> members) {
		Assert.requiredArgument(members != null, "members");
		this.sourceClass = members.sourceClass;
		this.processor = members.processor;
		this.stream = members.stream;
		this.with = members.with;
	}

	@Override
	public Members<T> all() {
		Members<T> members = clone();
		members.withStream(streamAll());
		return members;
	}

	public Function<Class<?>, ? extends Stream<T>> getProcessor() {
		return processor;
	}

	@Override
	public Members<T> clone() {
		Members<T> clone = new Members<T>(this.sourceClass, this.processor);
		if (this.with != null) {
			clone.with = this.with.clone();
		}

		if (this.with != null) {
			clone.stream = this.stream;
		}

		if (this.shared != null) {
			clone.shared = this.shared;
		}
		return clone;
	}

	/**
	 * 过滤
	 * 
	 * @see #map(Processor)
	 * @see Stream#filter(Predicate)
	 * @param predicate
	 * @return
	 */
	public Members<T> filter(Predicate<? super T> predicate) {
		if (predicate == null) {
			return this;
		}
		return mapProcessor((s) -> s == null ? s : s.filter(predicate));
	}

	/**
	 * 排除
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<T> exclude(Predicate<? super T> predicate) {
		if (predicate == null) {
			return this;
		}

		return filter(predicate.negate());
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
	 * @see #streamAll()
	 * @see Stream#findFirst()
	 * @return
	 */
	public Optional<T> findFirst() {
		return streamAll().findFirst();
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

	@Override
	public Class<?> getCursorId() {
		return sourceClass;
	}

	@Override
	public List<T> getList() {
		if (this.shared != null) {
			return Collections.unmodifiableList(this.shared);
		}

		return stream().collect(Collectors.toList());
	}

	@Override
	public Class<?> getNextCursorId() {
		return with == null ? null : with.sourceClass;
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	@Override
	public boolean hasNext() {
		return with != null;
	}

	@Override
	public Members<T> jumpTo(Class<?> cursorId) {
		return new Members<>(sourceClass, this.processor);
	}

	@Override
	public <TT> Members<TT> map(Function<? super T, TT> map) {
		return mapProcessor((s) -> s == null ? null : s.map(map));
	}

	/**
	 * 映射
	 * 
	 * @param processor
	 * @return 返回一个新的
	 */
	public <S> Members<S> mapProcessor(Function<Stream<T>, ? extends Stream<S>> processor) {
		Assert.requiredArgument(processor != null, "processor");
		return mapProcessor(processor, (e) -> processor.apply(this.processor.apply(e)), this.processor);
	}

	private <S> Members<S> mapProcessor(Function<Stream<T>, ? extends Stream<S>> processor,
			Function<Class<?>, ? extends Stream<S>> rootMapProcessor,
			Function<Class<?>, ? extends Stream<T>> rootProcessor) {
		Members<S> members = new Members<S>(this.sourceClass,
				this.processor == rootProcessor ? rootMapProcessor : ((e) -> processor.apply(this.processor.apply(e))));
		if (this.with != null) {
			members.with = this.with.mapProcessor(processor, rootMapProcessor, rootProcessor);
		}

		if (this.stream != null) {
			members.stream = processor.apply(this.stream);
		}

		if (this.shared != null) {
			members.shared = processor.apply(this.shared.stream()).collect(Collectors.toList());
		}
		return members;
	}

	@Override
	public Members<T> next() {
		return with;
	}

	public Members<T> shared() {
		Members<T> members = clone();
		if (members.shared != null) {
			members.shared = members.getList();
		}

		if (members.with != null) {
			members.with = members.with.shared();
		}
		return members;
	}

	/**
	 * 只获取当前的操作流
	 * 
	 * @return
	 */
	public Stream<T> stream() {
		if (shared != null) {
			return shared.stream();
		}

		Stream<T> stream = this.processor.apply(sourceClass);
		if (stream == null) {
			if (this.stream == null) {
				return StreamProcessorSupport.emptyStream();
			} else {
				return stream;
			}
		} else {
			if (this.stream == null) {
				return stream;
			} else {
				return Stream.concat(stream, this.stream);
			}
		}
	}

	public Members<T> with(Members<T> with) {
		if (this.with == null) {
			this.with = with;
		} else {
			this.with.with(with);
		}
		return this;
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
	 * 关联所有的接口和父类(不关联父类接口)
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<T> withAll(@Nullable Predicate<Class<?>> predicate) {
		return withInterfaces(predicate).withSuperclass(predicate);
	}

	public Members<T> withAll(@Nullable Predicate<Class<?>> predicate,
			Function<Class<?>, ? extends Stream<T>> processor) {
		return withInterfaces(predicate, processor).withSuperclass(predicate, processor);
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
	 * 关联类
	 * 
	 * @param sourceClass
	 * @param processor
	 * @return
	 */
	public Members<T> withClass(Class<?> sourceClass, Function<Class<?>, ? extends Stream<T>> processor) {
		return with(new Members<>(sourceClass, processor));
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
	 * 关联接口
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<T> withInterfaces(@Nullable Predicate<Class<?>> predicate) {
		return withInterfaces(predicate, this.processor);
	}

	/**
	 * 关联接口
	 * 
	 * @param predicate
	 * @param processor
	 * @return
	 */
	public Members<T> withInterfaces(@Nullable Predicate<Class<?>> predicate,
			Function<Class<?>, ? extends Stream<T>> processor) {
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

	public Members<T> withStream(Stream<T> stream) {
		if (this.stream == null) {
			this.stream = stream;
		} else {
			this.stream = Stream.concat(this.stream, stream);
		}
		return this;
	}

	/**
	 * 关联所有父类(不关联父类接口)
	 * 
	 * @return
	 */
	public Members<T> withSuperclass() {
		return withSuperclass(false);
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
	 * 关联父类
	 * 
	 * @param interfaces 是否也关联父类的接口
	 * @param predicate
	 * @return
	 */
	public Members<T> withSuperclass(boolean interfaces, @Nullable Predicate<Class<?>> predicate) {
		return withSuperclass(interfaces, predicate, this.processor);
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
			Function<Class<?>, ? extends Stream<T>> processor) {
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
	 * 关联父类(不关联父类接口)
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<T> withSuperclass(@Nullable Predicate<Class<?>> predicate) {
		return withSuperclass(false, predicate);
	}

	public Members<T> withSuperclass(@Nullable Predicate<Class<?>> predicate,
			Function<Class<?>, ? extends Stream<T>> processor) {
		return withSuperclass(false, predicate, processor);
	}
}
