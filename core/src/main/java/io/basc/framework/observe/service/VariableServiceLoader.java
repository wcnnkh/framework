package io.basc.framework.observe.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.observe.container.ServiceRegistry;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.observe_old.Observable;
import io.basc.framework.util.observe_old.Observer;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.watch.Variable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VariableServiceLoader<S> extends Observer<ChangeEvent> implements ServiceLoader<S>, Variable {
	@NonNull
	private final ServiceLoader<? extends S> serviceLoader;
	@NonNull
	private final NavigableElementRegistry<S> registry;
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
