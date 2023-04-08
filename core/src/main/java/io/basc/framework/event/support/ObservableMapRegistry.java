package io.basc.framework.event.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ChangeType;
import io.basc.framework.event.Observable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ElementSet;
import io.basc.framework.util.Elements;
import io.basc.framework.util.MapCombiner;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Selector;

/**
 * 对于大量的数据，且不频繁修改时可以节省空间,因为此类会对数据进行合并
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public class ObservableMapRegistry<K, V> extends ObservableRegistry<Map<K, V>> {
	/**
	 * 主节点，控制map类型和最高优先级数据
	 */
	private final DynamicMap<K, V> master;
	private final BroadcastEventDispatcher<ChangeEvent<Elements<K>>> keyEventDispatcher;
	private final Function<? super Properties, ? extends Map<K, V>> propertiesMapper;

	public ObservableMapRegistry(Function<? super Properties, ? extends Map<K, V>> propertiesMapper) {
		this(new ConcurrentHashMap<>(), propertiesMapper);
	}

	public ObservableMapRegistry(DynamicMap<K, V> master,
			BroadcastEventDispatcher<ChangeEvent<Elements<K>>> keyEventDispatcher,
			Function<? super Properties, ? extends Map<K, V>> propertiesMapper) {
		Assert.requiredArgument(master != null, "master");
		Assert.requiredArgument(keyEventDispatcher != null, "keyEventDispatcher");
		Assert.requiredArgument(propertiesMapper != null, "propertiesMapper");
		this.master = master;
		this.keyEventDispatcher = keyEventDispatcher;
		this.propertiesMapper = propertiesMapper;
		// 默认使用合并策略
		setSelector(new MapCombiner<>());

		master.getEventDispatcher().registerListener((e) -> {
			Set<K> changeKeys = e.getChangeType() == ChangeType.DELETE ? e.getOldSource().keySet()
					: e.getSource().keySet();
			keyEventDispatcher.publishEvent(new ChangeEvent<>(e, new ElementSet<>(changeKeys)));
			// 这里只触发更新，不触发节点变更，以防止出现事件循环
			touchValue();
		});

		getElementEventDispatcher().registerListener((e) -> {
			Set<K> keys = new LinkedHashSet<>();
			for (Observable<Map<K, V>> observable : e.getSource()) {
				Map<K, V> map = observable.orElse(Collections.emptyMap());
				keys.addAll(map.keySet());
			}
			keyEventDispatcher.publishEvent(new ChangeEvent<>(e, new ElementSet<>(keys)));
		});
	}

	public ObservableMapRegistry(Map<K, V> customMap,
			Function<? super Properties, ? extends Map<K, V>> propertiesMapper) {
		this(new DynamicMap<>(customMap), new StandardBroadcastEventDispatcher<>(), propertiesMapper);
	}

	/**
	 * 主要数据, 默认情况下此优先级最高，对此进行修改会触发数据重新收集
	 * 
	 * @return
	 */
	public DynamicMap<K, V> getMaster() {
		return master;
	}

	/**
	 * 所有key的变更事件
	 * 
	 * @return
	 */
	public BroadcastEventDispatcher<ChangeEvent<Elements<K>>> getKeyEventDispatcher() {
		return keyEventDispatcher;
	}

	public Function<? super Properties, ? extends Map<K, V>> getPropertiesMapper() {
		return propertiesMapper;
	}

	public Map<K, V> getReadonlyMap() {
		return orElse(Collections.emptyMap());
	}

	public final Registration registerProperties(Observable<? extends Properties> properties) {
		Assert.requiredArgument(properties != null, "properties");
		Function<? super Properties, ? extends Map<K, V>> mapper = getPropertiesMapper();
		Observable<Map<K, V>> observable = properties.map(mapper);
		return register(observable);
	}

	@Override
	protected Map<K, V> select() {
		Map<K, V> map = super.select();
		if (CollectionUtils.isEmpty(map)) {
			return Collections.unmodifiableMap(master.getUnsafeMap());
		}

		Map<K, V> approximateMap = CollectionFactory.createApproximateMap(master.getUnsafeMap(), 16);
		Map<K, V> applyMap = getSelector().apply(Arrays.asList(map, master.getUnsafeMap()));
		approximateMap.putAll(applyMap);
		return Collections.unmodifiableMap(approximateMap);
	}

	@Override
	public void setSelector(Selector<Map<K, V>> selector) {
		Assert.requiredArgument(selector != null, "selector");
		super.setSelector(selector);
	}
}
