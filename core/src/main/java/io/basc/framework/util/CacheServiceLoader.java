package io.basc.framework.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 该实现存在缓存
 * 
 * @author wcnnkh
 *
 * @param <S>
 */
public class CacheServiceLoader<S> extends HierarchicalServiceLoader<S> implements Consumer<S> {
	private volatile LinkedHashSet<S> cache;
	private final ServiceLoaderRegistry<Consumer<? super S>> aware = new ServiceLoaderRegistry<>();

	/*
	 * public ConfigurableServiceLoader1() {
	 * getElementEventDispatcher().registerListener((event) -> { reloadCache(false);
	 * });
	 * 
	 * aware.getElementEventDispatcher().registerListener((event) -> {
	 * reloadCache(true); }); }
	 */

	/**
	 * 服务的初始化行为
	 */
	@Override
	public void accept(S service) {
		for (Consumer<? super S> consumer : aware) {
			consumer.accept(service);
		}
	}

	public boolean contains(S service) {
		initCache();
		return cache.contains(service);
	}

	public ServiceLoaderRegistry<Consumer<? super S>> getAware() {
		return aware;
	}

	private void initCache() {
		if (cache == null) {
			synchronized (this) {
				if (cache == null) {
					this.cache = load(false);
				}
			}
		}
	}

	@Override
	public boolean isEmpty() {
		initCache();
		return cache.isEmpty();
	}

	@Override
	public Iterator<S> iterator() {
		initCache();
		return cache.iterator();
	}

	private LinkedHashSet<S> load(boolean forceAware) {
		LinkedHashSet<S> set = new LinkedHashSet<>();
		Iterator<S> iterator = super.iterator();
		while (iterator.hasNext()) {
			S service = iterator.next();
			if (service == null) {
				continue;
			}

			// 一定要判断cache不为空才做contains判断，不然会出现死循环,
			// 因为默认的contains调用的initCache方法，initCache在cache为空的情况下又调用了load
			if (forceAware || cache == null || !contains(service)) {
				// 如果服务不存在那么可以初始化
				accept(service);
			}
			set.add(service);
		}
		return set;
	}

	@Override
	public void reload() {
		try {
			super.reload();
		} finally {
			reloadCache(false);
		}
	}

	private void reloadCache(boolean forceAware) {
		if (cache != null) {
			synchronized (this) {
				if (cache != null) {
					cache = load(forceAware);
				}
			}
		}
	}

	@Override
	public final Stream<S> stream() {
		return Streams.stream(spliterator());
	}

	@Override
	public String toString() {
		return toList().toString();
	}
}
