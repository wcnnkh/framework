package io.basc.framework.factory;

import java.util.Collection;
import java.util.Collections;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;

public class BeanResolverConfiguration implements BeanResolver {
	private boolean aopEnable;
	// 默认使用单例
	private boolean singleton = true;
	private boolean nullable;

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor) {
		return null;
	}

	@Override
	public String getId(TypeDescriptor typeDescriptor) {
		return null;
	}

	@Override
	public Collection<String> getNames(TypeDescriptor typeDescriptor) {
		return Collections.emptyList();
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

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return false;
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		return null;
	}

	@Override
	public boolean isNullable(ParameterDescriptor parameterDescriptor) {
		return nullable;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public boolean isExternal(TypeDescriptor typeDescriptor) {
		return false;
	}

}
