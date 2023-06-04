package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.core.ResolvableType;
import lombok.Getter;

@Getter
public class SingletonFactoryBean<T> implements FactoryBean<T> {
	private final ResolvableType type;
	private final T bean;

	public SingletonFactoryBean(T bean) {
		this(bean == null ? null : ResolvableType.forClass(bean.getClass()), bean);
	}

	public SingletonFactoryBean(Class<T> type, T bean) {
		this(type == null ? null : ResolvableType.forClass(type), bean);
	}

	public SingletonFactoryBean(ResolvableType type, T bean) {
		this.type = type;
		this.bean = bean;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public T get() {
		return bean;
	}

}
