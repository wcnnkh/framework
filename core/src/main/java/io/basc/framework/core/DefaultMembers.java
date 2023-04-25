package io.basc.framework.core;

import java.util.function.Function;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public class DefaultMembers<E> implements Members<E>, Cloneable {
	private final ResolvableType source;
	private volatile Elements<E> elements;
	private final Function<? super ResolvableType, ? extends Elements<E>> processor;

	public DefaultMembers(ResolvableType source, @Nullable Elements<E> elements) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
		this.elements = elements;
		this.processor = null;
	}

	public void setElements(Elements<E> elements) {
		this.elements = elements;
	}

	public DefaultMembers(ResolvableType source,
			@Nullable Function<? super ResolvableType, ? extends Elements<E>> processor) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
		this.processor = processor;
	}

	public DefaultMembers(ResolvableType source, Elements<E> elements,
			@Nullable Function<? super ResolvableType, ? extends Elements<E>> processor) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
		this.elements = elements;
		this.processor = processor;
	}

	public DefaultMembers(DefaultMembers<E> members) {
		Assert.requiredArgument(members != null, "members");
		this.source = members.source;
		this.elements = members.elements;
		this.processor = members.processor;
	}

	@Override
	public DefaultMembers<E> clone() {
		// 浅拷贝
		return new DefaultMembers<>(this);
	}

	@Override
	public final ResolvableType getSource() {
		return source;
	}

	@Override
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

	@Nullable
	public Function<? super ResolvableType, ? extends Elements<E>> getProcessor() {
		return processor;
	}

	@Override
	public <T> Members<T> convert(Function<? super Elements<E>, ? extends Elements<T>> converter) {
		return new DefaultMembers<>(source, this.elements == null ? null : converter.apply(this.elements),
				this.processor == null ? null : (type) -> {
					Elements<E> es = this.processor.apply(type);
					return es == null ? null : converter.apply(es);
				});
	}
}
