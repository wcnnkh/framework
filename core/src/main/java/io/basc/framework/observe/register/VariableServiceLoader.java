package io.basc.framework.observe.register;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.limit.DisposableLimiter;
import io.basc.framework.observe.ChangeEvent;
import io.basc.framework.observe.Observable;
import io.basc.framework.observe.Observer;
import io.basc.framework.observe.Variable;
import io.basc.framework.observe.container.ServiceRegistry;
import io.basc.framework.register.PayloadRegistration;
import io.basc.framework.register.Registration;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VariableServiceLoader<S> extends Observer<ChangeEvent> implements ServiceLoader<S>, Variable {
	@NonNull
	private final ServiceLoader<? extends S> serviceLoader;
	@NonNull
	private final ServiceRegistry<S> registry;
	private volatile Elements<PayloadRegistration<S>> registrations;
	private volatile long lastModified = 0L;

	private PayloadRegistration<S> register(S service) {
		if (registry == null) {
			return new PayloadRegistration<S>(Registration.EMPTY, service);
		} else {
			Registration registration = registry.register(service);
			return new PayloadRegistration<S>(new DisposableLimiter(), registration, service);
		}
	}

	public void reload(boolean reloadServiceLoader) {
		synchronized (this) {
			if (reloadServiceLoader) {
				serviceLoader.reload();
			}

			registerObserver();
			if (registrations != null) {
				for (PayloadRegistration<S> registration : registrations) {
					registration.unregister();
				}
			}

			// TODO 优化一下，不用加载全部
			List<PayloadRegistration<S>> list = new ArrayList<>();
			for (S service : serviceLoader.getServices()) {
				PayloadRegistration<S> registration = register(service);
				if (registration.isInvalid()) {
					continue;
				}

				list.add(registration);
			}
			registrations = list.isEmpty() ? Elements.empty() : Elements.of(new ArrayList<>(list));
			// TODO 触发事件

			// 应该使用记数器还是时间戳?
			lastModified = System.currentTimeMillis();
		}
	}

	@Override
	public void reload() {
		reload(true);
	}

	private volatile Registration registration;

	private void registerObserver() {
		if (registration == null && serviceLoader instanceof Observable) {
			synchronized (this) {
				if (registration == null && serviceLoader instanceof Observable) {
					Observable<?> observable = (Observable<?>) serviceLoader;
					registration = observable.registerBatchListener((events) -> {
						reload(false);
					});
				}
			}
		}
	}

	@Override
	public Elements<S> getServices() {
		synchronized (this) {
			if (registrations == null) {
				reload(false);
			}
			return registrations.map((e) -> e.getPayload());
		}
	}

	@Override
	public long lastModified() throws IOException {
		return lastModified;
	}

}
