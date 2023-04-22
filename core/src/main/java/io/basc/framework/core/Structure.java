package io.basc.framework.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.MultiElements;
import lombok.ToString;

/**
 * 类的结构
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
@ToString
public class Structure<E> implements Cloneable {
	private volatile Elements<Structure<E>> interfaces;
	private volatile Members<E> members;
	private volatile Function<? super ResolvableType, ? extends Elements<E>> processor;
	@Nullable
	private volatile Structure<E> superclass;

	public Structure() {
	}

	public Structure(Members<E> members) {
		Assert.requiredArgument(members != null, "members");
		this.members = members;
	}

	public Structure(ResolvableType source, Function<? super ResolvableType, ? extends Elements<E>> processor) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(processor != null, "processor");
		this.processor = processor;
		Elements<E> elements = processor.apply(source);
		this.members = new Members<>(source, elements == null ? Elements.empty() : elements);
	}

	public Members<E> getMembers() {
		return members;
	}

	public Structure<E> getSuperclass() {
		if (superclass == null && processor != null) {
			synchronized (this) {
				if (superclass == null && processor != null) {
					Members<E> members = getMembers();
					if (members == null) {
						return null;
					}

					ResolvableType superType = members.getSource().getSuperType();
					if (superType == null) {
						return null;
					}

					Elements<E> elements = processor.apply(superType);
					if (elements == null) {
						return null;
					}

					this.superclass = new Structure<>(new Members<>(superType, elements));
					this.superclass.processor = this.processor;
				}
			}
		}
		return superclass;
	}

	public Elements<Structure<E>> getInterfaces() {
		if (interfaces == null && processor != null) {
			synchronized (this) {
				if (interfaces == null && processor != null) {
					Members<E> members = getMembers();
					if (members == null) {
						return null;
					}

					ResolvableType[] interfaceTypes = members.getSource().getInterfaces();
					if (interfaceTypes == null || interfaceTypes.length == 0) {
						return null;
					}

					List<Structure<E>> list = new ArrayList<>(interfaceTypes.length);
					for (ResolvableType source : interfaceTypes) {
						Elements<E> elements = processor.apply(source);
						if (elements == null) {
							return null;
						}

						Structure<E> structure = new Structure<>(new Members<>(source, elements));
						structure.processor = this.processor;
						list.add(structure);
					}
					interfaces = Elements.of(list);
				}
			}
		}
		return interfaces;
	}

	/**
	 * 返回一个新的Structure，包含全部元素
	 * 
	 * @return
	 */
	public Structure<E> all() {
		Members<E> members = new Members<>(this.members.getSource(), recursionElements());
		return new Structure<>(members);
	}

	@Override
	public Structure<E> clone() {
		Structure<E> structure = new Structure<>(this.members.clone());
		structure.processor = this.processor;
		if (this.superclass != null) {
			structure.superclass = this.superclass.clone();
		}

		if (this.interfaces != null) {
			structure.interfaces = this.interfaces.map((e) -> e.clone());
		}
		return structure;
	}

	public Structure<E> concat(Elements<? extends E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		return flatConvert((e) -> e.concat(elements));
	}

	/**
	 * 转换并返回一个新的
	 * 
	 * @param <T>
	 * @param converter
	 * @return
	 */
	public <T> Structure<T> convert(Function<? super Members<E>, ? extends Members<T>> converter) {
		Assert.requiredArgument(converter != null, "converter");
		Members<T> target = converter.apply(this.members);
		if (target == null) {
			target = new Members<>(this.members.getSource(), Elements.empty());
		}
		Structure<T> structure = new Structure<>(target);
		structure.processor = (source) -> {
			Members<E> members = this.processor.apply(source);
			if (members == null) {
				return null;
			}
			return converter.apply(members);
		};

		if (this.superclass != null) {
			structure.superclass = this.superclass.convert(converter);
		}

		if (this.interfaces != null) {
			structure.interfaces = this.interfaces.map((m) -> m.convert(converter));
		}
		return structure;
	}

	public Structure<E> filter(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((m) -> m.filter(predicate));
	}

	/**
	 * 直接映射当前members
	 * 
	 * @param mapper
	 * @return
	 */
	public Structure<E> flatConvert(Function<? super Members<E>, ? extends Members<E>> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		Structure<E> structure = new Structure<>(mapper.apply(this.members));
		structure.processor = this.processor;
		if (this.superclass != null) {
			structure.superclass = this.superclass.clone();
		}

		if (this.interfaces != null) {
			structure.interfaces = this.interfaces.map((e) -> e.clone());
		}
		return structure;
	}

	public <T> Structure<T> map(Function<? super E, ? extends T> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return convert((m) -> m.map(mapper));
	}

	public Structure<E> peek(Consumer<? super E> consumer) {
		Assert.requiredArgument(consumer != null, "consumer");
		return convert((m) -> m.convert((e) -> e.peek(consumer)));
	}

	/**
	 * 递归获取所有的members
	 * 
	 * @return
	 */
	public Elements<Members<E>> recursion() {
		if (superclass == null) {
			if (interfaces == null) {
				return Elements.singleton(this.members);
			} else {
				return Elements.concat(Elements.singleton(this.members), interfaces.flatMap((e) -> e.recursion()));
			}
		} else {
			if (interfaces == null) {
				return Elements.concat(Elements.singleton(this.members), superclass.recursion());
			} else {
				return new MultiElements<>(Arrays.asList(Elements.singleton(this.members), superclass.recursion(),
						interfaces.flatMap((e) -> e.recursion())));
			}
		}
	}

	/**
	 * 递归获取所有元素
	 * 
	 * @return
	 */
	public Elements<E> recursionElements() {
		return recursion().flatMap((e) -> e.getElements()).distinct();
	}

	public void withAll() {
		if (processor == EMPTY_PROCESSOR) {
			return;
		}
		withAll(processor);
	}

	/**
	 * 调用{@link #withAll(Function, Predicate)}
	 * 
	 * @param processor
	 */
	public final void withAll(Function<? super ResolvableType, ? extends Members<E>> processor) {
		withAll(processor, null);
	}

	/**
	 * 关联所有，关联父类再关联接口，国为类有父类，但接口没有父类
	 * 
	 * @param processor
	 * @param predicate
	 */
	public void withAll(Function<? super ResolvableType, ? extends Members<E>> processor,
			@Nullable Predicate<? super ResolvableType> predicate) {
		withSuperclass(processor, predicate);
		withInterfaces(processor, predicate);
	}

	public void withInterfaces() {
		if (processor == EMPTY_PROCESSOR) {
			return;
		}

		withInterfaces(processor);
	}

	/**
	 * 调用{@link #withInterfaces(Function, Predicate)}
	 * 
	 * @param processor
	 */
	public final void withInterfaces(Function<? super ResolvableType, ? extends Members<E>> processor) {
		withInterfaces(processor, null);
	}

	/**
	 * 关联所有接口
	 * 
	 * @param processor
	 * @param predicate
	 */
	public void withInterfaces(Function<? super ResolvableType, ? extends Members<E>> processor,
			@Nullable Predicate<? super ResolvableType> predicate) {
		if (interfaces == null) {
			synchronized (this) {
				if (interfaces == null) {
					ResolvableType[] interfaces = this.members.getSource().getInterfaces();
					if (interfaces == null || interfaces.length == 0) {
						return;
					}

					List<Structure<E>> target = new ArrayList<>(interfaces.length);
					for (int i = 0; i < interfaces.length; i++) {
						ResolvableType interfaceClass = interfaces[i];
						if (predicate != null && !predicate.test(interfaceClass)) {
							// 断言不通过
							continue;
						}

						Members<E> members = processor.apply(interfaceClass);
						if (members == null) {
							continue;
						}

						Structure<E> structure = new Structure<>(members);
						target.add(structure);
						structure.withInterfaces(processor, predicate);
					}
					this.interfaces = Elements.of(target);
				}
			}
		}
	}

	public void withSuperclass() {
		if (processor == EMPTY_PROCESSOR) {
			return;
		}

		withSuperclass(processor);
	}

	/**
	 * 调用{@link #withSuperclass(Function, Predicate)}
	 * 
	 * @param processor
	 */
	public final void withSuperclass(Function<? super ResolvableType, ? extends Members<E>> processor) {
		withSuperclass(processor, null);
	}

	/**
	 * 关联父类
	 * 
	 * @param processor
	 * @param predicate
	 */
	public void withSuperclass(Function<? super ResolvableType, ? extends Members<E>> processor,
			@Nullable Predicate<? super ResolvableType> predicate) {
		Assert.requiredArgument(processor != null, "processor");
		if (this.superclass == null) {
			synchronized (this) {
				if (superclass == null) {
					ResolvableType superclass = this.members.getSource().getSuperType();
					if (superclass == null) {
						return;
					}

					if (predicate != null && !predicate.test(superclass)) {
						// 断言不通过
						return;
					}

					Members<E> members = processor.apply(superclass);
					if (members == null) {
						return;
					}

					Structure<E> structure = new Structure<>(members);
					// 递归引用所有父类
					structure.withSuperclass(processor, predicate);
					this.superclass = structure;
				}
			}
		}
	}
}
