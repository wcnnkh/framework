package io.basc.framework.context;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.util.Iterator;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Assert;

public class ContextResolverChain extends ContextResolverConfiguration implements ContextResolver {
	public static ContextResolverChain build(Iterator<? extends ContextResolverExtend> iterator) {
		return new ContextResolverChain(iterator);
	}

	public static ContextResolverChain build(Iterator<? extends ContextResolverExtend> iterator,
			ContextResolver nextChain) {
		return new ContextResolverChain(iterator, nextChain);
	}

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

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		if (iterator.hasNext()) {
			return iterator.next().match(metadataReader, metadataReaderFactory, this);
		}
		return nextChain == null ? super.match(metadataReader, metadataReaderFactory)
				: nextChain.match(metadataReader, metadataReaderFactory);
	}

	@Override
	public BeanDefinition resolveBeanDefinition(Class<?> sourceClass) {
		if (iterator.hasNext()) {
			return iterator.next().resolveBeanDefinition(sourceClass, this);
		}
		return nextChain == null ? super.resolveBeanDefinition(sourceClass)
				: nextChain.resolveBeanDefinition(sourceClass);
	}

	@Override
	public BeanDefinition resolveBeanDefinition(Class<?> sourceClass, Executable executable) {
		if (iterator.hasNext()) {
			return iterator.next().resolveBeanDefinition(sourceClass, executable, this);
		}
		return nextChain == null ? super.resolveBeanDefinition(sourceClass, executable)
				: nextChain.resolveBeanDefinition(sourceClass, executable);
	}

	@Override
	public boolean canResolveExecutable(Class<?> sourceClass) {
		if (iterator.hasNext()) {
			return iterator.next().canResolveExecutable(sourceClass, this);
		}
		return nextChain == null ? super.canResolveExecutable(sourceClass)
				: nextChain.canResolveExecutable(sourceClass);
	}

}
