package io.basc.framework.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.support.DynamicElementRegistry;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;

public class ServiceLoaderRegistry<S> extends DynamicElementRegistry<ServiceLoader<S>> implements ServiceLoader<S> {

	public ServiceLoaderRegistry() {
		this(new StandardBroadcastEventDispatcher<>());
	}

	public ServiceLoaderRegistry(
			BroadcastEventDispatcher<ChangeEvent<Elements<ServiceLoader<S>>>> elementEventDispatcher) {
		super(new CopyOnWriteArraySet<>(), elementEventDispatcher);
	}

	public boolean isEmpty() {
		for (ServiceLoader<S> serviceLoader : getElements()) {
			if (!serviceLoader.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public final Registration registerService(S service) {
		Assert.requiredArgument(service != null, "service");
		return registerServices(Arrays.asList(service));
	}

	public final Registration registerServices(Iterable<S> services) {
		Assert.requiredArgument(services != null, "services");
		Elements<S> elements = Elements.of(services);
		return registerElements(elements);
	}

	public final Registration registerElements(Elements<S> services) {
		Assert.requiredArgument(services != null, "services");
		return register(new StaticServiceLoader<>(services));
	}

	@Override
	public ElementRegistration<ServiceLoader<S>> registers(Iterable<? extends ServiceLoader<S>> elements) {
		ElementRegistration<ServiceLoader<S>> registration = super.registers(elements);
		if (registration.isEmpty()) {
			return registration;
		}

		Registration and = Registration.EMPTY;
		for (ServiceLoader<S> element : registration.getElements()) {
			if (element instanceof ServiceLoaderRegistry) {
				and = and.and(((ServiceLoaderRegistry<S>) element).getElementEventDispatcher()
						.registerListener((e) -> getElementEventDispatcher().publishEvent(e)));
			}
		}
		return registration.and(and);
	}

	@Override
	public void reload() {
		ConsumeProcessor.consumeAll(getElements(), (e) -> e.reload());
	}

	@Override
	public Stream<S> stream() {
		return getElements().stream().flatMap((e) -> e.stream());
	}

	@Override
	public Iterator<S> iterator() {
		return CollectionUtils.iterator(getElements().iterator(), (e) -> e.iterator());
	}
}
