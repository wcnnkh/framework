package io.basc.framework.context;

import java.util.Iterator;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.util.Assert;

public class ContextResolverChain extends ContextResolverConfiguration implements ContextResolver {
	private final Iterator<? extends ContextResolverExtend> iterator;
	private final ContextResolver nextChain;

	public ContextResolverChain(Iterator<? extends ContextResolverExtend> iterator) {
		this(iterator, null);
	}

	ContextResolverChain(Iterator<? extends ContextResolverExtend> iterator, ContextResolver nextChain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	@Override
	public ProviderDefinition getProviderDefinition(Class<?> clazz) {
		if (iterator.hasNext()) {
			return iterator.next().getProviderDefinition(clazz, this);
		}
		return nextChain == null ? super.getProviderDefinition(clazz) : nextChain.getProviderDefinition(clazz);
	}

	@Override
	public boolean hasContext(ParameterDescriptor parameterDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().hasContext(parameterDescriptor, this);
		}
		return nextChain == null ? super.hasContext(parameterDescriptor) : nextChain.hasContext(parameterDescriptor);
	}

	public static ContextResolverChain build(Iterator<? extends ContextResolverExtend> iterator) {
		return new ContextResolverChain(iterator);
	}

	public static ContextResolverChain build(Iterator<? extends ContextResolverExtend> iterator,
			ContextResolver nextChain) {
		return new ContextResolverChain(iterator, nextChain);
	}

}
