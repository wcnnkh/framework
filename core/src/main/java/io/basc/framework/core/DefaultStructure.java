package io.basc.framework.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

/**
 * 结构
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public class DefaultStructure<E> implements Structure<E>, Cloneable, Consumer<E> {
	@Nullable
	private volatile Elements<? extends DefaultStructure<E>> interfaces;
	private final Members<E> members;
	@Nullable
	private final Function<? super ResolvableType, ? extends Elements<E>> processor;
	@Nullable
	private volatile DefaultStructure<E> superclass;

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

	public DefaultStructure(Class<?> source, @Nullable Function<? super Class<?>, ? extends Elements<E>> processor) {
		this(ResolvableType.forClass(Assert.requiredArgument(source != null, "source", source)),
				processor == null ? null : (type) -> processor.apply(type.getRawClass()));
	}

	public DefaultStructure(DefaultStructure<E> structure) {
		Assert.requiredArgument(structure != null, "structure");
		this.members = structure.members;
		this.processor = structure.processor;
		this.interfaces = structure.interfaces;
		this.superclass = structure.superclass;
	}

	public DefaultStructure(Members<E> members,
			@Nullable Function<? super ResolvableType, ? extends Elements<E>> processor) {
		Assert.requiredArgument(members != null, "members");
		this.members = members;
		this.processor = processor;
	}

	public DefaultStructure(ResolvableType source,
			@Nullable Function<? super ResolvableType, ? extends Elements<E>> processor) {
		Assert.requiredArgument(source != null, "source");
		this.processor = processor == null ? null : (s) -> {
			Elements<E> elements = processor.apply(s);
			if (elements == null) {
				return null;
			}
			return elements.peek(this);
		};
		this.members = new DefaultMembers<>(source, this.processor);
	}

	@Override
	public DefaultStructure<E> clone() {
		DefaultStructure<E> structure = new DefaultStructure<>(this.members, this.processor);
		if (this.superclass != null) {
			structure.superclass = this.superclass.clone();
		}

		if (this.interfaces != null) {
			structure.interfaces = this.interfaces.map((e) -> e.clone());
		}
		return structure;
	}

	public <T> DefaultStructure<T> convert(Function<? super Elements<E>, ? extends Elements<T>> converter) {
		Assert.requiredArgument(converter != null, "converter");
		DefaultStructure<T> structure = new DefaultStructure<>(this.members.convert(converter), (source) -> {
			Elements<E> ms = processor.apply(source);
			if (ms == null) {
				return null;
			}
			return converter.apply(ms);
		});

		if (this.superclass != null) {
			structure.superclass = this.superclass.convert(converter);
		}

		if (this.interfaces != null) {
			structure.interfaces = this.interfaces.map((e) -> e.convert(converter));
		}
		return structure;
	}

	public DefaultStructure<E> exclude(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return map((e) -> e.exclude(predicate));
	}

	public DefaultStructure<E> filter(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return map((e) -> e.filter(predicate));
	}

	@Override
	public Elements<? extends DefaultStructure<E>> getInterfaces() {
		if (interfaces == null && processor != null) {
			ResolvableType[] interfaceTypes = getMembers().getSource().getInterfaces();
			if (interfaceTypes == null || interfaceTypes.length == 0) {
				return Elements.empty();
			}

			synchronized (this) {
				if (interfaces == null && processor != null) {
					List<DefaultStructure<E>> list = new ArrayList<>(interfaceTypes.length);
					for (ResolvableType interfaceType : interfaceTypes) {
						list.add(new DefaultStructure<>(interfaceType, processor));
					}
					interfaces = Elements.of(list);
				}
			}
		}
		return interfaces == null ? Elements.empty() : interfaces;
	}

	@Override
	public DefaultStructure<E> getMembers() {
		return new DefaultStructure<>(this.members, null);
	}

	public final Function<? super ResolvableType, ? extends Elements<E>> getProcessor() {
		return processor;
	}

	public DefaultStructure<E> getSuperclass() {
		if (superclass == null && processor != null) {
			ResolvableType superType = getMembers().getSource().getSuperType();
			if (superType == null) {
				return null;
			}

			synchronized (this) {
				if (superclass == null && processor != null) {
					this.superclass = new DefaultStructure<>(superType, processor);
				}
			}
		}
		return superclass;
	}

	public DefaultStructure<E> map(Function<? super Elements<E>, ? extends Elements<E>> mapper) {
		Assert.requiredArgument(mapper != null, "converter");
		return convert(mapper);
	}

	public void setInterfaces(Elements<? extends DefaultStructure<E>> interfaces) {
		this.interfaces = interfaces;
	}

	public void setSuperclass(DefaultStructure<E> superclass) {
		this.superclass = superclass;
	}

	@Override
	public DefaultStructure<E> all() {
		Members<E> members = Structure.super.all();
		// 不传播processor，防止重复加载superclass和interfaces
		return new DefaultStructure<>(members, null);
	}

	@Override
	public Elements<? extends DefaultStructure<E>> recursion() {
		return Structure.super.recursion().map((e) -> new DefaultStructure<>(e, null));
	}

	public DefaultStructure<E> with(Function<? super Members<E>, ? extends Members<E>> withProcessor) {
		Assert.requiredArgument(withProcessor != null, "withProcessor");
		Members<E> members = withProcessor.apply(this.members);
		if (members == null) {
			members = new DefaultMembers<>(this.members.getSource(), Elements.empty());
		}

		DefaultStructure<E> structure = new DefaultStructure<>(members, this.processor);
		structure.superclass = this.superclass;
		structure.interfaces = this.interfaces;
		return structure;
	}
}
