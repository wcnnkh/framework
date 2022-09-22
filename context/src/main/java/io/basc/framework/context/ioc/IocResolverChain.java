package io.basc.framework.context.ioc;

import java.lang.reflect.Method;
import java.util.Iterator;

import com.sun.istack.internal.Nullable;

import io.basc.framework.core.parameter.ParameterDescriptor;

public class IocResolverChain extends IocResolverConfiguration implements IocResolver {
	private final Iterator<? extends IocResolverExtend> iterator;
	private final IocResolver nextChain;

	public IocResolverChain(Iterator<? extends IocResolverExtend> iterator, @Nullable IocResolver nextChain) {
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	@Override
	public ValueDefinition resolveValueDefinition(ParameterDescriptor parameterDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().resolveValueDefinition(parameterDescriptor, this);
		}
		return nextChain == null ? super.resolveValueDefinition(parameterDescriptor)
				: nextChain.resolveValueDefinition(parameterDescriptor);
	}

	@Override
	public MethodIocDefinition resolveInitDefinition(Method method) {
		if (iterator.hasNext()) {
			return iterator.next().resolveInitDefinition(method, this);
		}
		return nextChain == null ? super.resolveInitDefinition(method) : nextChain.resolveInitDefinition(method);
	}

	@Override
	public MethodIocDefinition resolveDestroyDefinition(Method method) {
		if (iterator.hasNext()) {
			return iterator.next().resolveDestroyDefinition(method, this);
		}
		return nextChain == null ? super.resolveDestroyDefinition(method) : nextChain.resolveDestroyDefinition(method);
	}

	@Override
	public AutowiredDefinition resolveAutowiredDefinition(ParameterDescriptor parameterDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().resolveAutowiredDefinition(parameterDescriptor, this);
		}
		return nextChain == null ? super.resolveAutowiredDefinition(parameterDescriptor)
				: nextChain.resolveAutowiredDefinition(parameterDescriptor);
	}

}
