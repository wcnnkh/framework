package io.basc.framework.beans;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.util.Elements;

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
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).isSingleton(type);
	}

	@Override
	public String getId(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).getId(typeDescriptor);
	}

	@Override
	public Collection<String> getNames(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).getNames(typeDescriptor);
	}

	@Override
	public boolean isAopEnable(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).isAopEnable(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).resolveDependenceProcessors(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).resolveInitProcessors(typeDescriptor);
	}

	@Override
	public Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).resolveDestroyProcessors(typeDescriptor);
	}

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).getDefaultParameter(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).isAccept(parameterDescriptor);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).getParameter(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptors parameterDescriptors) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).isAccept(parameterDescriptors);
	}

	@Override
	public Elements<? extends Parameter> getParameters(ParameterDescriptors parameterDescriptors) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).getParameters(parameterDescriptors);
	}

	@Override
	public boolean isNullable(ParameterDescriptor parameterDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).isNullable(parameterDescriptor);
	}

	@Override
	public boolean isExternal(TypeDescriptor typeDescriptor) {
		return BeanResolverChain.build(getServices().iterator(), getDefaultResolver()).isExternal(typeDescriptor);
	}
}
