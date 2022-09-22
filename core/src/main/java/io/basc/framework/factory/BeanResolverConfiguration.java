package io.basc.framework.factory;

import java.util.Collection;
import java.util.Collections;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;

public class BeanResolverConfiguration extends AbstractParametersFactory implements BeanResolver {
	private boolean aopEnable;
	private InstanceFactory instanceFactory;
	private boolean singleton;

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor) {
		return null;
	}

	@Override
	public String getId(TypeDescriptor typeDescriptor) {
		return typeDescriptor.getType().getName();
	}

	public InstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	private String getInstanceName(ParameterDescriptor parameterDescriptor) {
		if (getInstanceFactory().isInstance(parameterDescriptor.getType())) {
			return parameterDescriptor.getType().getName();
		}
		return null;
	}

	@Override
	public Collection<String> getNames(TypeDescriptor typeDescriptor) {
		return Collections.emptyList();
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		String name = getInstanceName(parameterDescriptor);
		return name == null ? null : getInstanceFactory().getInstance(name);
	}

	@Override
	protected Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) throws Exception {
		return getParameter(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		if (parameterDescriptor.isNullable()) {
			return true;
		}

		String name = getInstanceName(parameterDescriptor);
		if (name == null) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean isAccept(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		if (parameterDescriptor.getType() == parameterDescriptors.getDeclaringClass()) {
			return false;
		}
		return isAccept(parameterDescriptor);
	}

	public boolean isAopEnable() {
		return aopEnable;
	}

	@Override
	public boolean isAopEnable(TypeDescriptor typeDescriptor) {
		return aopEnable;
	}

	public boolean isSingleton() {
		return singleton;
	}

	@Override
	public boolean isSingleton(TypeDescriptor type) {
		return isSingleton();
	}

	@Override
	public Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor) {
		return Collections.emptyList();
	}

	@Override
	public Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor) {
		return Collections.emptyList();
	}

	@Override
	public Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor) {
		return Collections.emptyList();
	}

	public void setAopEnable(boolean aopEnable) {
		this.aopEnable = aopEnable;
	}

	public void setInstanceFactory(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
}
