package io.basc.framework.util.register.container;

import java.util.function.Supplier;

import io.basc.framework.util.register.ServiceBatchRegistration;
import io.basc.framework.util.register.ServiceRegistration;
import io.basc.framework.util.register.ServiceRegistry;

public abstract class AbstractServiceRegistry<S, C, R extends ServiceRegistration<S>, RS extends ServiceBatchRegistration<S, R>>
		extends AbstractRegistry<S, C, R, RS> implements ServiceRegistry<S, R> {

	public AbstractServiceRegistry(Supplier<? extends C> containerSupplier) {
		super(containerSupplier);
	}

	@Override
	public void reload() {
		// ignore 这是一个静态容器，一般都会主动清理注销的数据，所以默认可以忽略
	}
}
