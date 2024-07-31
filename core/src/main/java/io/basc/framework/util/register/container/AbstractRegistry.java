package io.basc.framework.util.register.container;

import java.util.Arrays;
import java.util.function.Supplier;

import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registrations;
import io.basc.framework.util.register.Registry;

public abstract class AbstractRegistry<E, C, R extends ContainerRegistration<E>> extends LazyContainer<C>
		implements Registry<E, R> {

	public AbstractRegistry(Supplier<? extends C> containerSupplier) {
		super(containerSupplier);
	}

	@Override
	public final R register(E registration) throws RegistrationException {
		// 强制使用批量注册
		Registrations<R> registrations = registers(Arrays.asList(registration));
		return registrations.getRegistrations().first();
	}

	/**
	 * 创建一个批处理
	 * 
	 * @param items
	 * @return
	 */
	protected abstract BatchRegistration<R> createBatchRegistration(Iterable<? extends E> items);

	/**
	 * 注册后的行为
	 * 
	 * @param batchRegistration
	 */
	protected abstract BatchRegistration<R> batch(BatchRegistration<R> batchRegistration);

	@Override
	public final BatchRegistration<R> registers(Iterable<? extends E> items) throws RegistrationException {
		BatchRegistration<R> containerBatchRegistration = write((collection) -> {
			BatchRegistration<R> batchRegistration = createBatchRegistration(items);
			for (R registration : batchRegistration.getRegistrations()) {
				if (!register(collection, registration)) {
					registration.getLimiter().limited();
				}
			}
			return batchRegistration;
		});

		return batch(containerBatchRegistration);
	}

	/**
	 * 会有线程安全的环境中执行
	 * 
	 * @param container
	 * @param registration
	 * @return
	 */
	protected abstract boolean register(C container, R registration);
}
