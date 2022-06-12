package io.basc.framework.factory.support;

import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.FactoryResolver;
import io.basc.framework.util.Assert;

public class FactoryResolverExtendChain extends FactoryResolverConfiguration implements FactoryResolver {
	private final Iterator<? extends FactoryResolverExtend> iterator;
	private final FactoryResolver nextChain;

	public FactoryResolverExtendChain(Iterator<? extends FactoryResolverExtend> iterator) {
		this(iterator, null);
	}

	FactoryResolverExtendChain(Iterator<? extends FactoryResolverExtend> iterator, FactoryResolver nextChain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	@Override
	public boolean isSingleton(TypeDescriptor type) {
		if (iterator.hasNext()) {
			return iterator.next().isSingleton(type, this);
		}
		return nextChain == null ? super.isSingleton(type) : nextChain.isSingleton(type);
	}

	public static FactoryResolverExtendChain build(Iterator<? extends FactoryResolverExtend> iterator) {
		return new FactoryResolverExtendChain(iterator);
	}

	public static FactoryResolverExtendChain build(Iterator<? extends FactoryResolverExtend> iterator,
			FactoryResolver factoryResolver) {
		return new FactoryResolverExtendChain(iterator, factoryResolver);
	}
}
