package io.basc.framework.core;

import java.util.ArrayList;
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
import io.basc.framework.util.ClassUtils;
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
	/**
	 * 直接关联不做任何限制
	 */
	public static final WithMethod DIRECT = new Direct();
	/**
	 * 忽略相同的sourceClass(默认的方法)
	 */
	public static final WithMethod REFUSE = new Refuse();

	/**
	 * 相同的sourceClass进行覆盖(代价较高)
	 */
	public static final WithMethod COVER = new Cover();

	@Nullable
	private List<T> members;
	private Function<Class<?>, ? extends Stream<T>> processor;
	private Class<?> sourceClass;
	@Nullable
	private Supplier<? extends Stream<T>> streamSupplier;

	@Nullable
	private Supplier<? extends Stream<T>> withStreamSupplier;
	@Nullable
	private Members<T> with;

	private WithMethod withMethod = REFUSE;

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
		this.streamSupplier = members.streamSupplier;
		this.withStreamSupplier = members.withStreamSupplier;
		this.with = members.with;
		this.members = members.members == null ? null : new ArrayList<T>(members.members);
		this.withMethod = members.withMethod;
	}

	public Members<T> withMethod(WithMethod method) {
		Assert.requiredArgument(method != null, "method");
		Members<T> members = clone();
		members.withMethod = method;
		return members;
	}

	@Override
	public Members<T> all() {
		Members<T> members = new Members<T>(this.sourceClass, this.processor);
		members.streamSupplier = () -> streamAll();
		return members;
	}

	@Override
	public Members<T> clone() {
		Members<T> clone = new Members<T>(this.sourceClass, this.processor);
		if (this.with != null) {
			clone.with = this.with.clone();
		}

		if (this.streamSupplier != null) {
			clone.streamSupplier = this.streamSupplier;
		}

		if (this.withStreamSupplier != null) {
			clone.withStreamSupplier = this.withStreamSupplier;
		}

		if (this.members != null) {
			clone.members = this.members;
		}

		clone.withMethod = this.withMethod;
		return clone;
	}

	public final boolean contains(Class<?> sourceClass) {
		if (sourceClass == null) {
			return false;
		}

		Members<T> members = this;
		while (members != null) {
			if (ClassUtils.sameName(members.sourceClass, sourceClass)) {
				return true;
			}
			members = members.with;
		}
		return false;
	}

	/**
	 * 去重
	 * 
	 * @return
	 */
	public Members<T> distinct() {
		Members<T> members = new Members<T>(this.sourceClass, this.processor);
		members.streamSupplier = () -> stream().distinct();
		if (members.with != null) {
			members.with = this.with.distinct();
		}
		return members;
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
	 * @see #streamAll()
	 * @see Stream#findAny()
	 * @return
	 * @throws E
	 */
	public final Optional<T> findAny() {
		return streamAll().findAny();
	}

	/**
	 * @see #streamAll()
	 * @see Stream#findFirst()
	 * @return
	 */
	public final Optional<T> findFirst() {
		return streamAll().findFirst();
	}

	@Nullable
	@Override
	public final T get() {
		return findAny().orElse(null);
	}

	@Override
	public final Class<?> getCursorId() {
		return sourceClass;
	}

	@Override
	public List<T> getList() {
		if (this.members != null) {
			return Collections.unmodifiableList(this.members);
		}

		return stream().collect(Collectors.toList());
	}

	@Override
	public final Class<?> getNextCursorId() {
		return with == null ? null : with.sourceClass;
	}

	public Function<Class<?>, ? extends Stream<T>> getProcessor() {
		return processor;
	}

	public final Class<?> getSourceClass() {
		return sourceClass;
	}

	@Override
	public final boolean hasNext() {
		return with != null;
	}

	@Override
	public Members<T> jumpTo(Class<?> cursorId) {
		return new Members<>(sourceClass, this.processor);
	}

	@Override
	public <TT> Members<TT> map(Function<? super T, ? extends TT> map) {
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

		if (this.streamSupplier != null) {
			members.streamSupplier = () -> (processor.apply(this.streamSupplier.get()));
		}

		if (this.members != null) {
			members.members = processor.apply(this.members.stream()).collect(Collectors.toList());
		}
		return members;
	}

	@Override
	public Members<T> next() {
		return with;
	}

	/**
	 * 直接设置当前的members
	 * 
	 * @param members
	 */
	public void setMembers(List<T> members) {
		this.members = members;
	}

	public Members<T> shared() {
		Members<T> members = clone();
		if (members.members != null) {
			members.members = members.getList();
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
		if (members != null) {
			return members.stream();
		}

		Stream<T> stream;
		if (streamSupplier == null) {
			stream = this.processor.apply(sourceClass);
		} else {
			stream = streamSupplier.get();
		}

		if (stream == null) {
			if (this.withStreamSupplier == null) {
				return StreamProcessorSupport.emptyStream();
			} else {
				return this.withStreamSupplier.get();
			}
		} else {
			if (this.withStreamSupplier == null) {
				return stream;
			} else {
				return Stream.concat(stream, this.withStreamSupplier.get());
			}
		}
	}

	public Members<T> with(Members<T> with) {
		if (with == null) {
			return this;
		}

		withMethod.with(with, this);
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
		return with(new Members<T>(sourceClass, this.processor));
	}

	/**
	 * 关联类
	 * 
	 * @param sourceClass
	 * @param processor
	 * @return
	 */
	public Members<T> withClass(Class<?> sourceClass, Predicate<? super T> predicate) {
		Members<T> members = new Members<T>(sourceClass,
				this.processor.andThen((e) -> e == null ? e : e.filter(predicate)));
		return with(members);
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

		Members<T> members = this;
		for (Class<?> interfaceClass : interfaces) {
			if (predicate == null || predicate.test(interfaceClass)) {
				members = members.with(new Members<T>(interfaceClass, processor));
			} else {
				break;
			}
		}
		return members;
	}

	public Members<T> withStream(Supplier<? extends Stream<T>> streamSupplier) {
		if (this.withStreamSupplier == null) {
			this.withStreamSupplier = streamSupplier;
		} else {
			this.withStreamSupplier = () -> Stream.concat(this.withStreamSupplier.get(), streamSupplier.get());
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
		Members<T> members = this;
		while (superclass != null) {
			if (predicate == null || predicate.test(superclass)) {
				members = members.with(new Members<T>(superclass, processor));
				if (interfaces) {
					members = members.withInterfaces(predicate, processor);
				}
			} else {
				break;
			}
			superclass = superclass.getSuperclass();
		}
		return members;
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

	public static interface WithMethod {
		<T> void with(Members<T> source, Members<T> target);
	}

	private static class Direct implements WithMethod {

		@Override
		public <T> void with(Members<T> source, Members<T> target) {
			if (source == null) {
				return;
			}

			// 使用循环而不使用递归
			Members<T> with = target;
			while (with.with != null) {
				with = with.with;
			}
			with.with = source;
		}
	}

	private static class Refuse extends Direct {
		@Override
		public <T> void with(Members<T> source, Members<T> target) {
			if (source == null) {
				return;
			}

			if (target.contains(source.sourceClass)) {
				with(source.with, target);
				return;
			}
			super.with(source, target);
		}
	}

	private static class Cover implements WithMethod {
		@Override
		public <T> void with(Members<T> source, Members<T> target) {
			if (source == null) {
				return;
			}

			Members<T> with = source;
			while (with != null) {
				Members<T> item = with.clone();
				item.with = null;

				boolean use = false;
				Members<T> targetWith = target;
				while (targetWith != null) {
					if (ClassUtils.sameName(item.sourceClass, targetWith.sourceClass)) {
						use = true;
						if (targetWith == target) {
							// 如果是父级，不能替换
							targetWith.members = item.getList();
						} else {
							targetWith.sourceClass = item.sourceClass;
							targetWith.members = item.members;
							targetWith.processor = item.processor;
							targetWith.streamSupplier = item.streamSupplier;
							targetWith.withStreamSupplier = item.withStreamSupplier;
						}
						break;
					}
					targetWith = targetWith.with;
				}

				if (!use) {
					targetWith.with = item;
				}
				with = with.with;
			}
		}
	}
}
