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
 * @param <M>
 */
public class Members<M> implements Cloneable, Pageables<Class<?>, M> {
	private static class Cover implements WithMethod {
		@Override
		public <M> void with(Members<M> source, Members<M> target) {
			if (source == null) {
				return;
			}

			Members<M> with = source;
			while (with != null) {
				Members<M> item = with.clone();
				item.with = null;

				boolean use = false;
				Members<M> targetWith = target;
				while (targetWith != null) {
					if (ClassUtils.sameName(item.sourceClass, targetWith.sourceClass)) {
						use = true;
						if (targetWith == target) {
							// 如果是父级，不能替换
							targetWith.elements = item.getElements();
						} else {
							targetWith.sourceClass = item.sourceClass;
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

	private static class Direct implements WithMethod {

		@Override
		public <M> void with(Members<M> source, Members<M> target) {
			if (source == null) {
				return;
			}

			// 使用循环而不使用递归
			Members<M> with = target;
			while (with.with != null) {
				with = with.with;
			}
			with.with = source;
		}
	}

	private static class Refuse extends Direct {
		@Override
		public <M> void with(Members<M> source, Members<M> target) {
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

	public static interface WithMethod {
		<M> void with(Members<M> source, Members<M> target);
	}

	/**
	 * 相同的sourceClass进行覆盖(代价较高)
	 */
	public static final WithMethod COVER = new Cover();
	/**
	 * 直接关联不做任何限制
	 */
	public static final WithMethod DIRECT = new Direct();
	/**
	 * 忽略相同的sourceClass(默认的方法)
	 */
	public static final WithMethod REFUSE = new Refuse();

	private volatile Elements<M> elements;

	private Class<?> sourceClass;

	@Nullable
	private Members<M> with;

	private WithMethod withMethod = REFUSE;

	public Members(Class<?> sourceClass, Elements<M> elements) {
		Assert.requiredArgument(sourceClass != null, "sourceClass");
		Assert.requiredArgument(elements != null, "elements");
		this.sourceClass = sourceClass;
		this.elements = elements;
	}

	public Members(Members<M> members) {
		Assert.requiredArgument(members != null, "members");
		this.sourceClass = members.sourceClass;
		this.elements = members.elements;
		this.with = members.with;
		this.withMethod = members.withMethod;
	}

	@Override
	public Members<M> all() {
		Members<M> members = new Members<M>(this.sourceClass, Pageables.super.all().getElements());
		members.withMethod = this.withMethod;
		return members;
	}

	@Override
	public Members<M> clone() {
		Members<M> clone = new Members<M>(this.sourceClass, this.elements);
		if (this.with != null) {
			clone.with = this.with.clone();
		}

		clone.withMethod = this.withMethod;
		return clone;
	}

	public Members<M> concat(Elements<? extends M> elements) {
		Assert.requiredArgument(elements != null, "elements");
		Members<M> clone = clone();
		clone.elements = Elements.concat(this.elements, elements);
		return clone;
	}

	public final boolean contains(Class<?> sourceClass) {
		if (sourceClass == null) {
			return false;
		}

		Members<M> members = this;
		while (members != null) {
			if (ClassUtils.sameName(members.sourceClass, sourceClass)) {
				return true;
			}
			members = members.with;
		}
		return false;
	}

	/**
	 * 映射
	 * 
	 * @param processor
	 * @return 返回一个新的
	 */
	@Override
	public <S> Members<S> convert(Function<? super Elements<M>, ? extends Elements<S>> processor) {
		Assert.requiredArgument(processor != null, "processor");
		Members<S> members = new Members<S>(this.sourceClass, processor.apply(this.elements));
		members.withMethod = this.withMethod;
		if (this.with != null) {
			members.with = this.with.convert(processor);
		}
		return members;
	}

	/**
	 * 去重
	 * 
	 * @return
	 */
	public Members<M> distinct() {
		return convert((e) -> e.convert((s) -> s.distinct()));
	}

	/**
	 * 排除
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<M> exclude(Predicate<? super M> predicate) {
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
	public Members<M> filter(Predicate<? super M> predicate) {
		if (predicate == null) {
			return this;
		}
		return convert((s) -> s == null ? s : s.filter(predicate));
	}

	@Override
	public <TT> Pageables<Class<?>, TT> flatMap(Function<? super M, ? extends Elements<TT>> mapper) {
		return convert((elements) -> elements.flatMap(mapper));
	}

	@Override
	public final Class<?> getCursorId() {
		return sourceClass;
	}

	@Override
	public Elements<M> getElements() {
		return elements;
	}

	@Override
	public final Class<?> getNextCursorId() {
		return with == null ? null : with.sourceClass;
	}

	public final Class<?> getSourceClass() {
		return sourceClass;
	}

	public WithMethod getWithMethod() {
		return withMethod;
	}

	@Override
	public final boolean hasNext() {
		return with != null;
	}

	@Nullable
	@Override
	public Members<M> jumpTo(Class<?> cursorId) {
		Members<M> members = this;
		while (members != null) {
			if (members.sourceClass == cursorId) {
				return members;
			}
			members = members.with;
		}
		return null;
	}

	public <TT> Members<TT> map(Function<? super M, ? extends TT> map) {
		return convert((s) -> s == null ? null : s.map(map));
	}

	@Override
	public Members<M> next() {
		return with;
	}

	@Override
	public Elements<? extends Pageable<Class<?>, M>> pages() {
		return Elements.of(() -> new PageablesIterator<>(this, (e) -> e.next()));
	}

	public Members<M> shared() {
		return convert((e) -> e.toList());
	}

	public Members<M> with(Members<M> with) {
		if (with == null) {
			return this;
		}

		withMethod.with(with, this);
		return this;
	}

	public Members<M> withAll(Function<Class<?>, ? extends Elements<M>> processor) {
		return withAll(processor, null);
	}

	/**
	 * 关联所有的接口和父类(不关联父类接口)
	 * 
	 * @param predicate
	 * @param processor
	 * @return
	 */
	public Members<M> withAll(Function<Class<?>, ? extends Elements<M>> processor,
			@Nullable Predicate<Class<?>> predicate) {
		return withInterfaces(processor, predicate).withSuperclass(processor, predicate);
	}

	/**
	 * 关联类
	 * 
	 * @param sourceClass
	 * @param predicate
	 * @return
	 */
	public Members<M> withClass(Class<?> sourceClass, Function<Class<?>, ? extends Elements<M>> processor) {
		Members<M> members = new Members<M>(sourceClass, processor.apply(sourceClass));
		return with(members);
	}

	public Members<M> withInterfaces(Function<Class<?>, ? extends Elements<M>> processor) {
		return withInterfaces(processor, null);

	}

	/**
	 * 该类上的所有接口(此方法不支持superclass的原因的，无法获取一个接口的父类，尝试获取一个接口的父类时始终为空)
	 * 
	 * @param predicate
	 * @param processor
	 * @return
	 */
	public Members<M> withInterfaces(Function<Class<?>, ? extends Elements<M>> processor,
			@Nullable Predicate<Class<?>> predicate) {
		Assert.requiredArgument(processor != null, "processor");
		Class<?>[] interfaces = this.sourceClass.getInterfaces();
		if (interfaces == null || interfaces.length == 0) {
			return this;
		}

		Members<M> members = this;
		for (Class<?> interfaceClass : interfaces) {
			if (predicate == null || predicate.test(interfaceClass)) {
				members = members.with(new Members<M>(interfaceClass, processor.apply(interfaceClass)));
			} else {
				break;
			}
		}
		return members;
	}

	public Members<M> withMethod(WithMethod method) {
		Assert.requiredArgument(method != null, "method");
		Members<M> members = clone();
		members.withMethod = method;
		return members;
	}

	public Members<M> withSuperclass(Function<Class<?>, ? extends Elements<M>> processor) {
		return withSuperclass(processor, false);
	}

	public Members<M> withSuperclass(Function<Class<?>, ? extends Elements<M>> processor, boolean interfaces) {
		return withSuperclass(processor, interfaces, null);
	}

	/**
	 * 关联所有父类
	 * 
	 * @param interfaces 是否关联父类的接口
	 * @param predicate
	 * @param processor
	 * @return
	 */
	public Members<M> withSuperclass(Function<Class<?>, ? extends Elements<M>> processor, boolean interfaces,
			@Nullable Predicate<Class<?>> predicate) {
		Assert.requiredArgument(processor != null, "processor");
		Class<?> superclass = this.sourceClass.getSuperclass();
		Members<M> members = this;
		while (superclass != null) {
			if (predicate == null || predicate.test(superclass)) {
				members = members.with(new Members<M>(superclass, processor.apply(superclass)));
				if (interfaces) {
					members = members.withInterfaces(processor, predicate);
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
	 * @param processor
	 * @param predicate
	 * @return
	 */
	public Members<M> withSuperclass(Function<Class<?>, ? extends Elements<M>> processor,
			@Nullable Predicate<Class<?>> predicate) {
		return withSuperclass(processor, false, predicate);
	}
}
