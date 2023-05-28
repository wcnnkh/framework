package io.basc.framework.beans.config;

import io.basc.framework.beans.FactoryBean;
import io.basc.framework.beans.Scope;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Assert;

public class SingletonFactoryBean<T> implements FactoryBean<T> {
	private final String name;
	private final Scope scope;
	private final TypeDescriptor typeDescriptor;
	private final T bean;

	public SingletonFactoryBean(String name, Scope scope, T bean) {
		this(name, scope, bean == null ? null : TypeDescriptor.forObject(bean), bean);
	}

	public SingletonFactoryBean(String name, Scope scope, Class<T> type, T bean) {
		this(name, scope, type == null ? null : TypeDescriptor.valueOf(type), bean);
	}

	public SingletonFactoryBean(String name, Scope scope, TypeDescriptor typeDescriptor, T bean) {
		Assert.requiredArgument(name != null, "name");
		Assert.requiredArgument(scope != null, "scope");
		this.name = name;
		this.scope = scope;
		this.typeDescriptor = typeDescriptor;
		this.bean = bean;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T orElse(T other) {
		return bean == null ? other : bean;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor == null ? TypeDescriptor.forObject(bean) : typeDescriptor;
	}

	@Override
	public Scope getScope() {
		return scope;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
