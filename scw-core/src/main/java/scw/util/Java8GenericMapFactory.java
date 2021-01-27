package scw.util;

import java.util.Map;

import scw.lang.RequiredJavaVersion;

@RequiredJavaVersion(8)
public class Java8GenericMapFactory implements GenericMapFactory {

	public <K, V> GenericMap<K, V> wrapper(Map<K, V> map) {
		return new Java8CompatibleMap<K, V>(map);
	}

	public static final class Java8CompatibleMap<K, V> extends DefaultGenericMap<K, V> {

		public Java8CompatibleMap(Map<K, V> targetMap) {
			super(targetMap);
		}

		@Override
		public V putIfAbsent(K key, V value) {
			return getTargetMap().putIfAbsent(key, value);
		}
		
		@Override
		public Java8CompatibleMap<K, V> clone() {
			return new Java8CompatibleMap<K, V>(super.clone());
		}
	}
}
