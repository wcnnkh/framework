package io.basc.framework.observe.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.container.AbstractServiceRegistry;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.PayloadBatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import lombok.NonNull;

public class EntryRegistry<K, V, M extends Map<K, PayloadRegistration<Entry<K, V>>>>
		extends AbstractServiceRegistry<Entry<K, V>, M> implements Map<K, V> {

	public EntryRegistry(@NonNull Supplier<? extends M> containerSupplier) {
		super(containerSupplier);
	}

	@Override
	public final PayloadRegistration<Entry<K, V>> register(Entry<K, V> element) throws RegistrationException {
		Registration registration = registers(Arrays.asList(element));
		return new PayloadRegistration<Map.Entry<K, V>>(registration, element);
	}

	protected final PayloadRegistration<Entry<K, V>> createElementRegistration(Entry<K, V> entry) {
		return new EntryRegistration(entry);
	}

	/**
	 * 重写了hashCode和equals方法，为了方便使用containsValue
	 * 
	 * @author shuchaowen
	 *
	 */
	private class EntryRegistration extends PayloadRegistration<Entry<K, V>> {

		public EntryRegistration(Entry<K, V> element) {
			super(new DisposableLimiter(), EMPTY, element);
		}

		@Override
		public void unregister(Runnable runnable) throws RegistrationException {
			super.unregister(() -> {
				execute((map) -> {
					if (map.remove(getPayload().getKey(), this)) {
						publishEvent(new RegistryEvent<>(EntryRegistry.this, ChangeType.DELETE, getPayload()));
						return true;
					}
					return false;
				});
			});
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (ObjectUtils.equals(getPayload().getValue(), obj)) {
				return true;
			}

			if (obj instanceof EntryRegistry.EntryRegistration) {
				EntryRegistration entryRegistration = (EntryRegistration) obj;
				return ObjectUtils.equals(getPayload().getValue(), entryRegistration.getPayload());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return ObjectUtils.hashCode(getPayload().getValue());
		}

		@Override
		public String toString() {
			return ObjectUtils.toString(getPayload().getValue());
		}
	}

	@Override
	public PayloadBatchRegistration<Entry<K, V>> registers(Iterable<? extends Entry<K, V>> elements)
			throws RegistrationException {
		PayloadBatchRegistration<Entry<K, V>> batchRegistration = write((map) -> {
			PayloadBatchRegistration<Entry<K, V>> payloadBatchRegistration = new PayloadBatchRegistration<>(elements);
			for (PayloadRegistration<Entry<K, V>> elementRegistration : payloadBatchRegistration.getServices()) {
				if (map.containsKey(elementRegistration.getPayload().getKey())) {
					elementRegistration.getLimiter().limited();
					continue;
				}

				map.put(elementRegistration.getPayload().getKey(), elementRegistration);
			}
			return payloadBatchRegistration;
		});

		batchRegistration = batch(batchRegistration);
		if (!batchRegistration.isInvalid()) {
			publishBatchEvent(batchRegistration.getServices(), ChangeType.CREATE);
		}
		return batchRegistration;
	}

	/**
	 * 批量推送事件
	 * 
	 * @param elements
	 * @param changeType
	 */
	private void publishBatchEvent(Elements<PayloadRegistration<Entry<K, V>>> elements, ChangeType changeType) {
		// 批量推送事件
		Elements<RegistryEvent<Entry<K, V>>> deleteEvents = elements
				.map((e) -> new RegistryEvent<>(this, changeType, e.getPayload()));
		if (!deleteEvents.isEmpty()) {
			publishBatchEvent(deleteEvents);
		}
	}

	protected PayloadBatchRegistration<Entry<K, V>> batch(PayloadBatchRegistration<Entry<K, V>> batchRegistration) {
		return batchRegistration.batch((elements) -> () -> {
			execute((map) -> {
				for (PayloadRegistration<Entry<K, V>> elementRegistration : elements) {
					map.remove(elementRegistration.getPayload().getKey(), elementRegistration);
				}
				return true;
			});

			publishBatchEvent(elements, ChangeType.DELETE);
		});
	}

	@Override
	public final PayloadBatchRegistration<Entry<K, V>> getRegistrations() {
		return read((map) -> {
			// 先拷贝出来，防止出现变化
			Set<PayloadRegistration<Entry<K, V>>> set = map == null ? Collections.emptySet()
					: map.entrySet().stream().map((e) -> e.getValue())
							.collect(Collectors.toCollection(LinkedHashSet::new));
			PayloadBatchRegistration<Entry<K, V>> batchRegistration = new PayloadBatchRegistration<>(Elements.of(set));
			return batch(batchRegistration);
		});
	}

	@Override
	public final Elements<Entry<K, V>> getServices() {
		return Elements.of(entrySet());
	}

	@Override
	public final int size() {
		return readInt((map) -> map == null ? 0 : map.size());
	}

	@Override
	public final boolean isEmpty() {
		return test((map) -> map == null ? true : map.isEmpty());
	}

	@Override
	public final boolean containsKey(Object key) {
		return test((map) -> map == null ? false : map.containsKey(key));
	}

	@Override
	public final boolean containsValue(Object value) {
		return read((map) -> map == null ? false : map.containsValue(value));
	}

	@Override
	public final V get(Object key) {
		return getValue((map) -> map.get(key));
	}

	@Override
	public final V put(K key, V value) {
		ObservableEntry<K, V> entry = new ObservableEntry<>(key, value);
		PayloadRegistration<Entry<K, V>> entryRegistration = createElementRegistration(entry);
		PayloadRegistration<Entry<K, V>> oldRegistration = write((map) -> map.put(key, entryRegistration));
		if (oldRegistration == null) {
			// 推送create事件
			publishEvent(new RegistryEvent<Map.Entry<K, V>>(this, ChangeType.CREATE, entry));
		} else {
			// update事件
			// 将旧的失效
			oldRegistration.getLimiter().limited();
			// 推送更新事件
			publishEvent(new RegistryEvent<Map.Entry<K, V>>(this, ChangeType.UPDATE, entry));
		}
		return oldRegistration == null ? null : oldRegistration.getPayload().getValue();
	}

	@Override
	public final V remove(Object key) {
		PayloadRegistration<Entry<K, V>> elementRegistration = update((map) -> {
			if (map == null) {
				return null;
			}

			return map.remove(key);
		});

		if (elementRegistration == null) {
			return null;
		}

		elementRegistration.getLimiter().limited();
		publishEvent(new RegistryEvent<Map.Entry<K, V>>(this, ChangeType.DELETE, elementRegistration.getPayload()));
		return elementRegistration.getPayload().getValue();
	}

	/**
	 * 获取值
	 * 
	 * @param getter 回调参数不会为空
	 * @return
	 */
	public final V getValue(Function<? super M, ? extends PayloadRegistration<Entry<K, V>>> getter) {
		PayloadRegistration<Entry<K, V>> elementRegistration = read((map) -> {
			if (map == null) {
				return null;
			}
			return getter.apply(map);
		});

		if (elementRegistration == null) {
			return null;
		}

		Entry<K, V> entry = elementRegistration.getPayload();
		return entry == null ? null : entry.getValue();
	}

	@Override
	public final void putAll(Map<? extends K, ? extends V> m) {
		write((map) -> {
			List<PayloadRegistration<Entry<K, V>>> createList = new ArrayList<>(m.size());
			List<PayloadRegistration<Entry<K, V>>> updateList = new ArrayList<>(m.size());
			for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
				ObservableEntry<K, V> observableEntry = new ObservableEntry<>(entry.getKey(), entry.getValue());
				EntryRegistration entryRegistration = new EntryRegistration(observableEntry);
				// TODO 待优化
				PayloadRegistration<Entry<K, V>> old = map.put(entry.getKey(), entryRegistration);
				if (old == null) {
					// create
					createList.add(entryRegistration);
				} else {
					old.getLimiter().limited();
					updateList.add(old);
				}
			}

			// 构造并推送批量事件
			Elements<RegistryEvent<Entry<K, V>>> createEvents = Elements.of(createList)
					.map((e) -> new RegistryEvent<>(this, ChangeType.CREATE, e.getPayload()));
			Elements<RegistryEvent<Entry<K, V>>> updateEvents = Elements.of(updateList)
					.map((e) -> new RegistryEvent<>(this, ChangeType.UPDATE, e.getPayload()));
			publishBatchEvent(createEvents.concat(updateEvents));
			return null;
		});
	}

	@Override
	public final void clear() {
		getRegistrations().unregister();
	}

	@Override
	public final Set<K> keySet() {
		return read((map) -> {
			if (map == null || map.isEmpty()) {
				return Collections.emptySet();
			}

			// 先拷贝出来，防止出现变化
			return map.keySet().stream().collect(Collectors.toCollection(LinkedHashSet::new));
		});
	}

	@Override
	public final Collection<V> values() {
		return read((map) -> {
			if (map == null || map.isEmpty()) {
				return Collections.emptyList();
			}

			// 先拷贝出来，防止出现变化
			return map.values().stream().map((e) -> e.getPayload().getValue())
					.collect(Collectors.toCollection(LinkedHashSet::new));
		});
	}

	@Override
	public final Set<Entry<K, V>> entrySet() {
		return read((map) -> {
			if (map == null || map.isEmpty()) {
				return Collections.emptySet();
			}

			return map.entrySet().stream().map((e) -> e.getValue().getPayload())
					.collect(Collectors.toCollection(LinkedHashSet::new));
		});
	}
}
