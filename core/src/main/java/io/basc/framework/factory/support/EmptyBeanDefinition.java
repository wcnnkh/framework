package io.basc.framework.factory.support;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeansException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public final class EmptyBeanDefinition implements BeanDefinition {
	private final String id;
	private final TypeDescriptor typeDescriptor;

	public EmptyBeanDefinition(TypeDescriptor typeDescriptor) {
		this(typeDescriptor, null);
	}

	public EmptyBeanDefinition(TypeDescriptor typeDescriptor, @Nullable String id) {
		Assert.requiredArgument(typeDescriptor != null, "typeDescriptor");
		this.id = id;
		this.typeDescriptor = typeDescriptor;
	}

	@Override
	public String getId() {
		return StringUtils.isEmpty(id) ? typeDescriptor.getType().getName() : id;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	@Override
	public Collection<String> getNames() {
		return Collections.emptyList();
	}

	@Override
	public boolean isInstance() {
		return false;
	}

	private BeansException getException() {
		return new BeansException("empty definition [" + getId() + "] type[" + getTypeDescriptor() + "]");
	}

	@Override
	public Object create() throws BeansException {
		throw getException();
	}

	@Override
	public Object create(Class<?>[] parameterTypes, Object[] params) throws BeansException {
		throw getException();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public boolean isInstance(Class<?>[] parameterTypes) {
		return false;
	}

	@Override
	public boolean isInstance(Object... params) {
		return false;
	}

	@Override
	public Object create(Object... params) throws BeansException {
		throw getException();
	}

	@Override
	public Iterator<ParameterDescriptors> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public void dependence(Object instance) throws BeansException {
	}

	@Override
	public void init(Object instance) throws BeansException {
	}

	@Override
	public void destroy(Object instance) throws BeansException {
	}
}
