package io.basc.framework.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.MergedElements;

/**
 * 成员
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public class Members<E> implements Cloneable, Consumer<E> {
	private final ResolvableType source;
	private volatile Elements<E> elements;
	private final Function<? super ResolvableType, ? extends Elements<E>> processor;
	@Nullable
	private volatile Elements<? extends Members<E>> interfaces;
	@Nullable
	private volatile Members<E> superclass;

	/**
	 * 接受此element
	 * <p>
	 * 
	 * 通过processor创建的element都会经过此方法
	 */
	@Override
	public void accept(E t) {
		// 默认没有实现
	}

	public Members(Members<E> members) {
		Assert.requiredArgument(members != null, "members");
		this.source = members.source;
		this.elements = members.elements;
		this.processor = members.processor;
		this.interfaces = members.interfaces;
		this.superclass = members.superclass;
	}

	public Members(Class<?> source, @Nullable final Function<? super Class<?>, ? extends Elements<E>> processor) {
		this(ResolvableType.forClass(Assert.requiredArgument(source != null, "source", source)), null,
				processor == null ? null : (type) -> processor.apply(type.getRawClass()));
	}

	/**
	 * 构造成员
	 * 
	 * @param source    来源
	 * @param elements  如果为空但processor不为空会自动加载
	 * @param processor 如果为空不会自动加载
	 */
	public Members(ResolvableType source, @Nullable Elements<E> elements,
			@Nullable final Function<? super ResolvableType, ? extends Elements<E>> processor) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
		this.elements = elements;
		this.processor = processor == null ? null : (s) -> {
			Elements<E> es = processor.apply(s);
			if (es == null) {
				return null;
			}
			return es.peek(this);
		};
	}

	@Override
	public Members<E> clone() {
		Members<E> members = new Members<>(this.source, null, this.processor);
		if (this.elements != null) {
			members.elements = this.elements;
		}
		if (this.superclass != null) {
			members.superclass = this.superclass.clone();
		}

		if (this.interfaces != null) {
			members.interfaces = this.interfaces.map((e) -> e.clone());
		}
		return members;
	}

	public <T> Members<T> convert(Function<? super Elements<E>, ? extends Elements<T>> converter) {
		Assert.requiredArgument(converter != null, "converter");
		Members<T> members = new Members<>(this.source, null, (source) -> {
			Elements<E> ms = processor.apply(source);
			if (ms == null) {
				return null;
			}
			return converter.apply(ms);
		});

		if (this.elements != null) {
			members.elements = converter.apply(this.elements);
		}

		if (this.superclass != null) {
			members.superclass = this.superclass.convert(converter);
		}

		if (this.interfaces != null) {
			members.interfaces = this.interfaces.map((e) -> e.convert(converter));
		}
		return members;
	}

	public Members<E> exclude(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return map((e) -> e.exclude(predicate));
	}

	public Members<E> filter(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return map((e) -> e.filter(predicate));
	}

	public Elements<? extends Members<E>> getInterfaces() {
		if (interfaces == null && processor != null) {
			ResolvableType[] interfaceTypes = this.source.getInterfaces();
			if (interfaceTypes == null || interfaceTypes.length == 0) {
				return Elements.empty();
			}

			synchronized (this) {
				if (interfaces == null && processor != null) {
					List<Members<E>> list = new ArrayList<>(interfaceTypes.length);
					for (ResolvableType interfaceType : interfaceTypes) {
						list.add(new Members<>(interfaceType, null, processor));
					}
					interfaces = Elements.of(list);
				}
			}
		}
		return interfaces == null ? Elements.empty() : interfaces;
	}

	public final Function<? super ResolvableType, ? extends Elements<E>> getProcessor() {
		return processor;
	}

	public final ResolvableType getSource() {
		return source;
	}

	public Members<E> getSuperclass() {
		if (superclass == null && processor != null) {
			ResolvableType superType = this.source.getSuperType();
			if (superType == null || superType == ResolvableType.NONE) {
				return null;
			}

			synchronized (this) {
				if (superclass == null && processor != null) {
					this.superclass = new Members<>(superType, null, processor);
				}
			}
		}
		return superclass;
	}

	public Members<E> map(Function<? super Elements<E>, ? extends Elements<E>> mapper) {
		Assert.requiredArgument(mapper != null, "converter");
		return convert(mapper);
	}

	public void setInterfaces(Elements<? extends Members<E>> interfaces) {
		this.interfaces = interfaces;
	}

	public void setSuperclass(Members<E> superclass) {
		this.superclass = superclass;
	}

	/**
	 * 全部
	 * 
	 * @return
	 */
	public Members<E> all() {
		// 不传播processor，防止重复加载superclass和interfaces
		return new Members<>(this.source, this.recursionElements(), null);
	}

	/**
	 * 递归所有成员
	 * 
	 * @return
	 */
	public Elements<? extends Members<E>> recursion() {
		Elements<? extends Members<E>> self = Elements.singleton(this);
		Members<E> superclass = getSuperclass();
		Elements<? extends Members<E>> interfaces = getInterfaces();
		if (superclass == null) {
			if (interfaces == null) {
				return self;
			} else {
				return new MergedElements<>(self, interfaces.flatMap((e) -> e.recursion()));
			}
		} else {
			if (interfaces == null) {
				return new MergedElements<>(self, superclass.recursion());
			} else {
				return new MergedElements<>(self, superclass.recursion(), interfaces.flatMap((e) -> e.recursion()));
			}
		}
	}

	/**
	 * 获取成员元素
	 * 
	 * @return
	 */
	public Elements<E> getElements() {
		if (elements == null && processor != null) {
			synchronized (this) {
				if (elements == null && processor != null) {
					this.elements = processor.apply(source);
					if (this.elements == null) {
						this.elements = Elements.empty();
					}
				}
			}
		}
		return this.elements == null ? Elements.empty() : this.elements;
	}

	/**
	 * 递归获取所有元素
	 * 
	 * @return
	 */
	public Elements<E> recursionElements() {
		return recursion().flatMap((e) -> e.getElements());
	}

	/**
	 * 关联元素
	 * 
	 * @param withProcessor
	 * @return
	 */
	public Members<E> with(Function<? super Elements<E>, ? extends Elements<E>> withProcessor) {
		Assert.requiredArgument(withProcessor != null, "withProcessor");
		Members<E> structure = new Members<>(this.source, null, this.processor);
		structure.elements = withProcessor.apply(this.getElements());
		if (structure.elements == null) {
			// 防止重复加载
			structure.elements = Elements.empty();
		}
		structure.superclass = this.superclass;
		structure.interfaces = this.interfaces;
		return structure;
	}

	public Members<E> concat(Elements<? extends E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		return with((e) -> e.concat(elements));
	}
}
