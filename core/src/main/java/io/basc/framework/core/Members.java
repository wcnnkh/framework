package io.basc.framework.core;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.page.Pageable;
import io.basc.framework.util.page.Pageables;
import io.basc.framework.util.page.PageablesIterator;

/**
 * 类成员
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public class Members<T> implements Cloneable, Pageables<Class<?>, T> {
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

	private Function<Class<?>, ? extends Elements<T>> processor;
	private Class<?> sourceClass;
	@Nullable
	private volatile Elements<T> elements;
	private WithMethod withMethod = REFUSE;
	@Nullable
	private Members<T> with;

	public Members(Class<?> sourceClass, Function<Class<?>, ? extends Elements<T>> processor) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(processor != null, "processor");
		this.sourceClass = sourceClass;
		this.processor = processor;
	}

	public Members(Members<T> members) {
		Assert.requiredArgument(members != null, "members");
		this.sourceClass = members.sourceClass;
		this.processor = members.processor;
		this.elements = members.elements;
		this.with = members.with;
		this.withMethod = members.withMethod;
	}

	public Members<T> withMethod(WithMethod method) {
		Assert.requiredArgument(method != null, "method");
		Members<T> members = clone();
		members.withMethod = method;
		return members;
	}

	public WithMethod getWithMethod() {
		return withMethod;
	}

	@Override
	public Elements<? extends Pageable<Class<?>, T>> pages() {
		return Elements.of(() -> new PageablesIterator<>(this, (e) -> e.next()));
	}

	@Override
	public Members<T> all() {
		Members<T> members = new Members<T>(this.sourceClass, this.processor);
		members.withMethod = this.withMethod;
		members.elements = Pageables.super.all().getElements();
		return members;
	}

	@Override
	public Members<T> clone() {
		Members<T> clone = new Members<T>(this.sourceClass, this.processor);
		if (this.with != null) {
			clone.with = this.with.clone();
		}

		if (this.elements != null) {
			clone.elements = this.elements;
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
		return convert((e) -> e.convert((s) -> s.distinct()));
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
	 * @see Stream#filter(Predicate)
	 * @param predicate
	 * @return
	 */
	public Members<T> filter(Predicate<? super T> predicate) {
		if (predicate == null) {
			return this;
		}
		return convert((s) -> s == null ? s : s.filter(predicate));
	}

	@Override
	public final Class<?> getCursorId() {
		return sourceClass;
	}

	@Override
	public final Class<?> getNextCursorId() {
		return with == null ? null : with.sourceClass;
	}

	public Function<Class<?>, ? extends Elements<T>> getProcessor() {
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

	public <TT> Members<TT> map(Function<? super T, ? extends TT> map) {
		return convert((s) -> s == null ? null : s.map(map));
	}

	/**
	 * 映射
	 * 
	 * @param processor
	 * @return 返回一个新的
	 */
	public <S> Members<S> convert(Function<? super Elements<T>, ? extends Elements<S>> processor) {
		Assert.requiredArgument(processor != null, "processor");
		return convert(processor, (e) -> processor.apply(this.processor.apply(e)), this.processor);
	}

	private <S> Members<S> convert(Function<? super Elements<T>, ? extends Elements<S>> processor,
			Function<Class<?>, ? extends Elements<S>> rootMapProcessor,
			Function<Class<?>, ? extends Elements<T>> rootProcessor) {
		Members<S> members = new Members<S>(this.sourceClass,
				this.processor == rootProcessor ? rootMapProcessor : ((e) -> processor.apply(this.processor.apply(e))));
		members.withMethod = this.withMethod;
		if (this.with != null) {
			members.with = this.with.convert(processor, rootMapProcessor, rootProcessor);
		}

		if (this.elements != null) {
			members.elements = processor.apply(this.elements);
		}
		return members;
	}

	@Override
	public Members<T> next() {
		return with;
	}

	public Members<T> shared() {
		return convert((e) -> e.toList());
	}

	@Override
	public Elements<T> getElements() {
		if (elements == null) {
			synchronized (this) {
				if (elements == null) {
					elements = processor.apply(sourceClass);
				}
			}
		}
		return elements;
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
			Function<Class<?>, ? extends Elements<T>> processor) {
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
	 * @param predicate
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
			Function<Class<?>, ? extends Elements<T>> processor) {
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

	public Members<T> concat(Elements<? extends T> elements) {
		Assert.requiredArgument(elements != null, "elements");
		Members<T> clone = clone();
		clone.elements = Elements.concat(this.elements, elements);
		return clone;
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
	 * @param interfaces 是否关联父类的接口
	 *                   {@link Members#withInterfaces(Predicate, Function)}
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
			Function<Class<?>, ? extends Elements<T>> processor) {
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
			Function<Class<?>, ? extends Elements<T>> processor) {
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
							targetWith.elements = item.getElements();
						} else {
							targetWith.sourceClass = item.sourceClass;
							targetWith.processor = item.processor;
							targetWith.elements = item.elements;
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
