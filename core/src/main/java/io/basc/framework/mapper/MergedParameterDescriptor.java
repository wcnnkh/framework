package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.annotation.MergedAnnotatedElement;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.select.Selector;
import lombok.RequiredArgsConstructor;

/**
 * 合并多个ParameterDescriptor
 * 
 * @author wcnnkh
 *
 */
@RequiredArgsConstructor
public class MergedParameterDescriptor<E extends ParameterDescriptor> implements ParameterDescriptor {
	private final String name;
	private final Elements<? extends E> elements;
	private Selector<E> selector = Selector.first();
	private volatile E master;
	private volatile TypeDescriptor typeDescriptor;

	public MergedParameterDescriptor(MergedParameterDescriptor<E> mergedParameterDescriptor) {
		Assert.requiredArgument(mergedParameterDescriptor != null, "mergedParameterDescriptor");
		this.name = mergedParameterDescriptor.name;
		this.elements = mergedParameterDescriptor.elements;
		this.selector = mergedParameterDescriptor.selector;
		this.master = mergedParameterDescriptor.master;
		this.typeDescriptor = mergedParameterDescriptor.typeDescriptor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null) {
			synchronized (this) {
				if (typeDescriptor == null) {
					MergedAnnotatedElement mergedAnnotatedElement = new MergedAnnotatedElement(
							elements.map((e) -> e.getTypeDescriptor()));
					TypeDescriptor masterTypeDescriptor = getMaster().getTypeDescriptor();
					this.typeDescriptor = new TypeDescriptor(masterTypeDescriptor.getResolvableType(),
							masterTypeDescriptor.getType(), mergedAnnotatedElement);
				}
			}
		}
		return typeDescriptor;
	}

	@Override
	public MergedParameterDescriptor<E> rename(String name) {
		MergedParameterDescriptor<E> mergedParameterDescriptor = new MergedParameterDescriptor<>(name, this.elements);
		mergedParameterDescriptor.master = this.master;
		mergedParameterDescriptor.selector = this.selector;
		mergedParameterDescriptor.typeDescriptor = this.typeDescriptor;
		return mergedParameterDescriptor;
	}

	public E getMaster() {
		if (master == null) {
			synchronized (this) {
				if (master == null) {
					this.master = selector.apply(this.elements);
				}
			}
		}
		return master;
	}

	public Selector<E> getSelector() {
		return selector;
	}

	public void setSelector(Selector<E> selector) {
		Assert.requiredArgument(selector != null, "selector");
		synchronized (this) {
			Assert.isTrue(master != null, "Master has already been selected, cannot set selector again");
		}
		this.selector = selector;
	}

	public Elements<? extends E> getElements() {
		return elements;
	}
}
