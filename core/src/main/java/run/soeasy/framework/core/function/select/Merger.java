package run.soeasy.framework.core.function.select;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import run.soeasy.framework.core.collection.CollectionFactory;
import run.soeasy.framework.core.collection.Elements;

/**
 * 合并器
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
@FunctionalInterface
public interface Merger<E> extends Selector<E> {
	public static class Flat<E> implements Merger<Elements<? extends E>> {
		private static final Flat<?> INSTANCE = new Flat<>();

		@Override
		public Elements<E> apply(Elements<? extends Elements<? extends E>> elements) {
			return elements.filter((e) -> e != null).flatMap((e) -> e.map(Function.identity()));
		}
	}

	public static class MapMerger<K, V> implements Merger<Map<K, V>> {
		private static final MapMerger<?, ?> INSTANCE = new MapMerger<>();

		@Override
		public Map<K, V> apply(Elements<? extends Map<K, V>> elements) {
			Map<K, V> target = null;
			for (Map<K, V> map : elements) {
				if (map == null || map.isEmpty()) {
					continue;
				}

				if (target == null) {
					target = CollectionFactory.createApproximateMap(map, 16);
				}

				target.putAll(map);
			}
			return target == null ? Collections.emptyMap() : target;
		}

	}

	public class PropertiesMerger implements Merger<Properties> {
		private static final PropertiesMerger INSTANCE = new PropertiesMerger();

		@Override
		public Properties apply(Elements<? extends Properties> elements) {
			Properties properties = new Properties();
			for (Properties props : elements) {
				properties.putAll(props);
			}
			return properties;
		}
	}

	/**
	 * 展开
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Merger<T> flat() {
		return (Merger<T>) Flat.INSTANCE;
	}

	/**
	 * 将多个map合并
	 * 
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> MapMerger<K, V> map() {
		return (MapMerger<K, V>) MapMerger.INSTANCE;
	}

	/**
	 * 将多个properties合并
	 * 
	 * @return
	 */
	public static PropertiesMerger properties() {
		return PropertiesMerger.INSTANCE;
	}
}
