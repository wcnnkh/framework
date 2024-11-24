package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.convert.TypeDescriptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class SingletonFactoryBean<T> implements FactoryBean<T> {
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final T bean;

	public SingletonFactoryBean(T bean) {
		this(bean == null ? null : ResolvableType.forClass(bean.getClass()), bean);
	}

	public SingletonFactoryBean(Class<T> type, T bean) {
		this(type == null ? null : ResolvableType.forClass(type), bean);
	}

	public SingletonFactoryBean(ResolvableType type, T bean) {
		this(TypeDescriptor.valueOf(type), bean);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public T getObject() {
		return bean;
	}
}
