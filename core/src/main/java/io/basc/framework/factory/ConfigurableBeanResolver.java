package io.basc.framework.factory;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterDescriptors;

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
		return BeanResolverChain.build(iterator(), getDefaultResolver()).isSingleton(type);
	}

	@Override
	public String getId(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).getId(typeDescriptor);
	}

	@Override
	public Collection<String> getNames(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).getNames(typeDescriptor);
	}

	@Override
	public boolean isAopEnable(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).isAopEnable(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver())
				.resolveDependenceProcessors(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).resolveInitProcessors(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).resolveDestroyProcessors(typeDescriptor);
	}

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).getDefaultParameter(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).isAccept(parameterDescriptor);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).getParameter(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptors parameterDescriptors) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).isAccept(parameterDescriptors);
	}

	@Override
	public Object[] getParameters(ParameterDescriptors parameterDescriptors) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).getParameters(parameterDescriptors);
	}

	@Override
	public boolean isNullable(ParameterDescriptor parameterDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).isNullable(parameterDescriptor);
	}

	@Override
	public boolean isExternal(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(iterator(), getDefaultResolver()).isExternal(typeDescriptor);
	}
}
