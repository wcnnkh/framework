package io.basc.framework.observe.register;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.container.AbstractServiceRegistry;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.PayloadBatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registry;

public class ObservableCollection<E, C extends Collection<PayloadRegistration<E>>> extends AbstractServiceRegistry<E, C>
		implements Collection<E> {

	public ObservableCollection(Supplier<? extends C> containerSupplier) {
		super(containerSupplier);
	}

	public final PayloadBatchRegistration<E> getRegistrations() {
		return read((members) -> {
			// copy保证线程安全
			List<PayloadRegistration<E>> list = members == null ? Collections.emptyList()
					: members.stream().filter((e) -> !e.isInvalid()).collect(Collectors.toList());
			PayloadBatchRegistration<E> batchRegistration = new PayloadBatchRegistration<>(Elements.of(list));
			return batch(batchRegistration);
		});
	}

	public final boolean contains(Object element) {
		return getServices().contains(element);
	}

	@Override
	public final Elements<E> getServices() {
		return read((members) -> {
			if (members == null) {
				return Elements.empty();
			}
			return Elements.of(members.stream().map((e) -> e.getPayload()).collect(Collectors.toList()));
		});
	}

	@Override
	public final PayloadRegistration<E> register(E element) {
		Registry<PayloadRegistration<E>> registrations = registers(Arrays.asList(element));
		return registrations.getServices().first();
	}

	protected PayloadBatchRegistration<E> batch(PayloadBatchRegistration<E> batchRegistration) {
		return batchRegistration.batch((services) -> () -> {
			// 清理无效数据
			cleanup();
			// 批量推送事件
			publishBatchEvent(services, ChangeType.DELETE);
		});
	}

	@Override
	public void reload() {
		cleanup();
	}

	private void cleanup() {
		execute((members) -> {
			Iterator<PayloadRegistration<E>> iterator = members.iterator();
			while (iterator.hasNext()) {
				PayloadRegistration<E> registration = iterator.next();
				if (registration.isInvalid()) {
					iterator.remove();
				}
			}
			return true;
		});
	}

	@Override
	public final PayloadBatchRegistration<E> registers(Iterable<? extends E> elements) throws RegistrationException {
		PayloadBatchRegistration<E> payloadBatchRegistration = write((members) -> {
			PayloadBatchRegistration<E> batchRegistration = new PayloadBatchRegistration<>(Elements.of(elements));
			for (PayloadRegistration<E> registration : batchRegistration.getServices()) {
				if (!members.add(registration)) {
					registration.getLimiter().limited();
				}
			}
			// 无需返回值
			return null;
		});

		payloadBatchRegistration = batch(payloadBatchRegistration);
		if (payloadBatchRegistration.isInvalid()) {
			publishBatchEvent(payloadBatchRegistration.getServices().filter((e) -> !e.isInvalid()), ChangeType.CREATE);
		}
		return payloadBatchRegistration;
	}

	protected void publishBatchEvent(Elements<PayloadRegistration<E>> registrations, ChangeType changeType) {
		Elements<RegistryEvent<E>> deleteEvents = registrations
				.map((e) -> new RegistryEvent<>(this, changeType, e.getPayload()));
		if (!deleteEvents.isEmpty()) {
			publishBatchEvent(deleteEvents);
		}
	}

	@Override
	public final int size() {
		return readInt((elements) -> elements == null ? 0 : elements.size());
	}

	@Override
	public final boolean isEmpty() {
		return test((members) -> members == null || members.isEmpty());
	}

	@Override
	public final Iterator<E> iterator() {
		return getServices().iterator();
	}

	@Override
	public final Object[] toArray() {
		return getServices().toArray();
	}

	@Override
	public final <T> T[] toArray(T[] a) {
		return getServices().toArray(a);
	}

	@Override
	public final boolean add(E e) {
		return addAll(Arrays.asList(e));
	}

	@Override
	public final boolean remove(Object o) {
		return removeAll(Arrays.asList(o));
	}

	@Override
	public final boolean containsAll(Collection<?> c) {
		return test((members) -> {
			if (members == null) {
				return false;
			}

			// registrations已经被重写，此处可以使用contains，也提高了使用Set时的性能
			return c.stream().allMatch((e) -> members.contains(e));
		});
	}

	@Override
	public final boolean addAll(Collection<? extends E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		Registry<PayloadRegistration<E>> registrations = registers(elements);
		return !registrations.isInvalid();
	}

	@Override
	public final boolean removeAll(Collection<?> c) {
		return execute((members) -> {
			boolean find = false;
			Iterator<PayloadRegistration<E>> iterator = members.iterator();
			while (iterator.hasNext()) {
				PayloadRegistration<E> registration = iterator.next();
				if (c.contains(registration.getPayload())) {
					iterator.remove();
					registration.unregister();
					find = true;
				}
			}
			return find;
		});
	}

	@Override
	public final boolean retainAll(Collection<?> c) {
		return test((members) -> {
			if (members == null) {
				return false;
			}

			// ElementRegistration 已经被重写，此处可以使用contains，也提高了使用Set时的性能
			return c.stream().anyMatch((e) -> members.contains(e));
		});
	}

	@Override
	public final void clear() {
		getRegistrations().unregister();
	}
}
