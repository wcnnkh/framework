package io.basc.framework.observe.register;

import java.util.Comparator;

import io.basc.framework.core.OrderComparator;
import io.basc.framework.util.register.PayloadBatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.Registration;
import lombok.Getter;

/**
 * 一个动态注入的实现
 * 
 * @author wcnnkh
 *
 * @param <S>
 */
@Getter
public class ObservableServiceLoader<S> extends ObservableSet<S> {
	private final ServiceInjectors<S> serviceInjectors = new ServiceInjectors<>();
	private final ServiceLoaderRegistry<S> serviceLoaderRegistry = new ServiceLoaderRegistry<>(this);

	public ObservableServiceLoader() {
		this(OrderComparator.INSTANCE);
	}

	public ObservableServiceLoader(Comparator<? super S> comparator) {
		super(comparator);
	}

	@Override
	protected PayloadBatchRegistration<S> batch(PayloadBatchRegistration<S> batchRegistration) {
		return super.batch(batchRegistration).batch((services) -> Registration
				.registers(services.map(PayloadRegistration::getPayload), serviceInjectors::inject));
	}
}
