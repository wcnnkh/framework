package io.basc.framework.event.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.ChangeType;
import io.basc.framework.event.NamedEventRegistry;
import io.basc.framework.event.ObservableChangeEvent;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.MapCombiner;
import io.basc.framework.util.Registration;

public class StandardObservableMap<K, V> extends StandardObservable<Map<K, V>>
		implements NamedEventRegistry<K, ChangeEvent<K>> {
	private final ObservableMap<K, V> sourceMap;
	private final MapCombiner<K, V> select = new MapCombiner<>();

	public StandardObservableMap() {
		this(new ConcurrentHashMap<>());
	}

	public StandardObservableMap(Map<K, V> sourceMap) {
		this(new StandardBroadcastEventDispatcher<>(), sourceMap);
	}

	public StandardObservableMap(BroadcastEventDispatcher<ObservableChangeEvent<Map<K, V>>> eventDispatcher,
			Map<K, V> sourceMap) {
		super(eventDispatcher);
		setSelector(select);
		this.sourceMap = new ObservableMap<K, V>(sourceMap, eventDispatcher) {
			@Override
			public void publishEvent(ObservableChangeEvent<Map<K, V>> event) {
				if (!getSources().isEmpty()) {
					getValueReference().set(select());
				}
				super.publishEvent(event);
			}
		};
	}

	public ObservableMap<K, V> getSourceMap() {
		return sourceMap;
	}

	@Override
	protected Map<K, V> select() {
		Map<K, V> map = super.select();
		if (CollectionUtils.isEmpty(map)) {
			return sourceMap.getTargetMap();
		}

		Map<K, V> approximateMap = CollectionFactory.createApproximateMap(sourceMap.getTargetMap(), 16);
		Map<K, V> applyMap = select.apply(Arrays.asList(map, sourceMap.getTargetMap()));
		approximateMap.putAll(applyMap);
		return map;
	}

	@Override
	public Registration registerListener(K name, EventListener<ChangeEvent<K>> eventListener) {
		return registerListener((e) -> {
			Map<K, V> oldMap = e.getOldSource();
			Map<K, V> newMap = e.getSource();
			if (CollectionUtils.isEmpty(oldMap)) {
				if (CollectionUtils.isEmpty(newMap)) {
					return;
				} else {
					ConsumeProcessor.consumeAll(newMap.keySet(), (key) -> eventListener
							.onEvent(new ChangeEvent<>(e.getCreateTime(), ChangeType.CREATE, key)));
				}
			} else {
				if (CollectionUtils.isEmpty(newMap)) {
					ConsumeProcessor.consumeAll(oldMap.keySet(), (key) -> eventListener
							.onEvent(new ChangeEvent<>(e.getCreateTime(), ChangeType.DELETE, key)));
				} else {
					List<K> keys = Stream.concat(oldMap.keySet().stream(), newMap.keySet().stream())
							.filter((key) -> !Objects.equals(oldMap.get(key), newMap.get(key)))
							.collect(Collectors.toList());
					ConsumeProcessor.consumeAll(keys, (key) -> eventListener
							.onEvent(new ChangeEvent<>(e.getCreateTime(), ChangeType.UPDATE, key)));
				}
			}
		});
	}

	@Override
	protected Map<K, V> getValue() {
		Map<K, V> map = super.getValue();
		if (CollectionUtils.isEmpty(map)) {
			map = sourceMap.getTargetMap();
		}
		return CollectionUtils.isEmpty(map) ? null : map;
	}

	public V get(K key) {
		return orElse(Collections.emptyMap()).get(key);
	}

	public int size() {
		return orElse(Collections.emptyMap()).size();
	}

	public boolean containsKey(Object key) {
		return orElse(Collections.emptyMap()).containsKey(key);
	}

	public boolean containsValue(Object value) {
		return orElse(Collections.emptyMap()).containsValue(value);
	}

	public Set<K> keySet() {
		return orElse(Collections.emptyMap()).keySet();
	}

	public Collection<V> values() {
		return orElse(Collections.emptyMap()).values();
	}

	public Set<Entry<K, V>> entrySet() {
		return orElse(Collections.emptyMap()).entrySet();
	}
}
