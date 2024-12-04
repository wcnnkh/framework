package io.basc.framework.core.execution.param;

import io.basc.framework.core.annotation.MergedAnnotatedElement;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.select.Selector;
import lombok.RequiredArgsConstructor;

/**
 * 合并多个ParameterDescriptor
 * 
 * @author wcnnkh
 *
 */
@RequiredArgsConstructor
public class MergedParameterDescriptor<E extends ParameterDescriptor>
		implements ParameterDescriptor, AnnotatedTypeMetadata {
	private final Elements<? extends E> elements;
	private volatile E master;
	private volatile String name;
	private Selector<E> selector = Selector.first();
	private volatile TypeDescriptor typeDescriptor;

	public MergedParameterDescriptor(MergedParameterDescriptor<E> mergedParameterDescriptor) {
		Assert.requiredArgument(mergedParameterDescriptor != null, "mergedParameterDescriptor");
		this.name = mergedParameterDescriptor.name;
		this.elements = mergedParameterDescriptor.elements;
		this.selector = mergedParameterDescriptor.selector;
		this.master = mergedParameterDescriptor.master;
		this.typeDescriptor = mergedParameterDescriptor.typeDescriptor;
	}

	public Elements<? extends E> getElements() {
		return elements;
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

	@Override
	public String getName() {
		if (name == null) {
			synchronized (this) {
				if (name == null) {
					name = getMaster().getName();
				}
			}
		}
		return name;
	}

	public Selector<E> getSelector() {
		return selector;
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
		MergedParameterDescriptor<E> mergedParameterDescriptor = new MergedParameterDescriptor<>(this.elements);
		mergedParameterDescriptor.name = name;
		mergedParameterDescriptor.master = this.master;
		mergedParameterDescriptor.selector = this.selector;
		mergedParameterDescriptor.typeDescriptor = this.typeDescriptor;
		return mergedParameterDescriptor;
	}

	public void setSelector(Selector<E> selector) {
		Assert.requiredArgument(selector != null, "selector");
		synchronized (this) {
			Assert.isTrue(master != null, "Master has already been selected, cannot set selector again");
		}
		this.selector = selector;
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return MergedAnnotations.from(elements);
	}
}
