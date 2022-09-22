package io.basc.framework.factory;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;

public class ConfigurableBeanResolver extends ConfigurableServices<BeanResolverExtend> implements BeanResolver {
	private BeanResolver defaultResolver;

	public ConfigurableBeanResolver() {
		super(BeanResolverExtend.class);
	}

	public BeanResolver getDefaultResolver() {
		return defaultResolver;
	}

	public void setDefaultResolver(BeanResolver defaultResolver) {
		this.defaultResolver = defaultResolver;
	}

	@Override
	public boolean isSingleton(TypeDescriptor type) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).isSingleton(type);
	}

	@Override
	public String getId(TypeDescriptor typeDescriptor) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).getId(typeDescriptor);
	}

	@Override
	public Collection<String> getNames(TypeDescriptor typeDescriptor) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).getNames(typeDescriptor);
	}

	@Override
	public boolean isAopEnable(TypeDescriptor typeDescriptor) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).isAopEnable(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver())
				.resolveDependenceProcessors(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).resolveInitProcessors(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).resolveDestroyProcessors(typeDescriptor);
	}

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).getDefaultParameter(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).isAccept(parameterDescriptor);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).getParameter(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptors parameterDescriptors) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).isAccept(parameterDescriptors);
	}

	@Override
	public Object[] getParameters(ParameterDescriptors parameterDescriptors) {
		return BeanResolverExtendChain.build(iterator(), getDefaultResolver()).getParameters(parameterDescriptors);
	}
}
