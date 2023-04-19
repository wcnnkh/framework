package io.basc.framework.core;

import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ObjectUtils;

/**
 * 类成员
 * 
 * @author wcnnkh
 *
 * @param <M>
 */
public class MembersSource<M> extends Members<M> {
	private Function<Class<?>, ? extends Elements<M>> processor;

	public MembersSource(Class<?> sourceClass, Function<Class<?>, ? extends Elements<M>> processor) {
		super(sourceClass, Assert.requiredArgument(processor, "processor", ObjectUtils::isNotEmpty).apply(sourceClass));
		this.processor = processor;
	}


	/**
	 * 关联所有(不关联父类接口)
	 * 
	 * @return
	 */
	public Members<M> withAll() {
		withAll(this.processor);
		return this;
	}

	/**
	 * 关联所有的接口和父类(不关联父类接口)
	 * 
	 * @param predicate
	 * @return
	 */
	public Members<M> withAll(@Nullable Predicate<Class<?>> predicate) {
		withAll(this.processor, predicate);
		return this;
	}

	/**
	 * 关联类
	 * 
	 * @param sourceClass
	 * @return
	 */
	public Members<M> withClass(Class<?> sourceClass) {
		withClass(sourceClass, this.processor);
		return this;
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
