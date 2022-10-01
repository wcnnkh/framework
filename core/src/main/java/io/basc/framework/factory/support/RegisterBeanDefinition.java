package io.basc.framework.factory.support;

import java.util.function.Supplier;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.lang.Nullable;

public class RegisterBeanDefinition extends FactoryBeanDefinition {
	private final Supplier<Boolean> isInstanceSupplier;
	private final Supplier<?> supplier;

	public RegisterBeanDefinition(BeanFactory beanFactory, TypeDescriptor typeDescriptor, String id, boolean singleton,
			@Nullable Supplier<Boolean> isInstanceSupplier, @Nullable Supplier<?> supplier) {
		super(beanFactory, typeDescriptor);
		setId(id);
		setSingleton(singleton);
		this.isInstanceSupplier = isInstanceSupplier;
		this.supplier = supplier;
	}

	@Override
	public boolean isInstance() {
		if (isInstanceSupplier == null) {
			return super.isInstance();
		}

		Boolean b = isInstanceSupplier.get();
		if (b == null) {
			return super.isInstance();
		}
		return b;
	}

	@Override
	public Object create() throws InstanceException {
		return supplier == null ? super.create() : supplier.get();
	}
}
