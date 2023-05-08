package io.basc.framework.factory;

import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public class BeanResolverChain extends BeanResolverConfiguration implements BeanResolver {
	private final Iterator<? extends BeanResolverExtend> iterator;
	private final BeanResolver nextChain;

	public BeanResolverChain(Iterator<? extends BeanResolverExtend> iterator) {
		this(iterator, null);
	}

	BeanResolverChain(Iterator<? extends BeanResolverExtend> iterator, BeanResolver nextChain) {
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

	@Override
	public String getId(TypeDescriptor typeDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getId(typeDescriptor, this);
		}

		return nextChain == null ? super.getId(typeDescriptor) : nextChain.getId(typeDescriptor);
	}

	@Override
	public Collection<String> getNames(TypeDescriptor typeDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getNames(typeDescriptor, this);
		}

		return nextChain == null ? super.getNames(typeDescriptor) : nextChain.getNames(typeDescriptor);
	}

	@Override
	public boolean isAopEnable(TypeDescriptor typeDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isAopEnable(typeDescriptor, this);
		}

		return nextChain == null ? super.isAopEnable(typeDescriptor) : nextChain.isAopEnable(typeDescriptor);
	}

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getDefaultParameter(parameterDescriptor, this);
		}

		return nextChain == null ? super.getDefaultParameter(parameterDescriptor)
				: nextChain.getDefaultParameter(parameterDescriptor);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getParameter(parameterDescriptor, this);
		}

		return nextChain == null ? super.getParameter(parameterDescriptor)
				: nextChain.getParameter(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isAccept(parameterDescriptor, this);
		}
		return nextChain == null ? super.isAccept(parameterDescriptor) : nextChain.isAccept(parameterDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().resolveDependenceProcessors(typeDescriptor, this);
		}
		return nextChain == null ? super.resolveDependenceProcessors(typeDescriptor)
				: nextChain.resolveDependenceProcessors(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().resolveDestroyProcessors(typeDescriptor, this);
		}
		return nextChain == null ? super.resolveDestroyProcessors(typeDescriptor)
				: nextChain.resolveDestroyProcessors(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().resolveInitProcessors(typeDescriptor, this);
		}

		return nextChain == null ? super.resolveInitProcessors(typeDescriptor)
				: nextChain.resolveInitProcessors(typeDescriptor);
	}

	@Override
	public Elements<? extends Parameter> getParameters(ParameterDescriptors parameterDescriptors) {
		if (iterator.hasNext()) {
			return iterator.next().getParameters(parameterDescriptors, this);
		}

		return nextChain == null ? super.getParameters(parameterDescriptors)
				: nextChain.getParameters(parameterDescriptors);
	}

	@Override
	public boolean isAccept(ParameterDescriptors parameterDescriptors) {
		if (iterator.hasNext()) {
			return iterator.next().isAccept(parameterDescriptors, this);
		}
		return nextChain == null ? super.isAccept(parameterDescriptors) : nextChain.isAccept(parameterDescriptors);
	}

	public static BeanResolverChain build(Iterator<? extends BeanResolverExtend> iterator) {
		return new BeanResolverChain(iterator);
	}

	public static BeanResolverChain build(Iterator<? extends BeanResolverExtend> iterator,
			BeanResolver nextChain) {
		return new BeanResolverChain(iterator, nextChain);
	}

	@Override
	public boolean isNullable(ParameterDescriptor parameterDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isNullable(parameterDescriptor, this);
		}
		return nextChain == null ? super.isNullable(parameterDescriptor) : nextChain.isNullable(parameterDescriptor);
	}

	@Override
	public boolean isExternal(TypeDescriptor typeDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isExternal(typeDescriptor, this);
		}
		return nextChain == null ? super.isExternal(typeDescriptor) : nextChain.isExternal(typeDescriptor);
	}
}
