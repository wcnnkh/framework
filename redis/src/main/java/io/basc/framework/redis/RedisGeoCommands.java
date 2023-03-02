package io.basc.framework.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Metric;
import io.basc.framework.data.geo.Point;

public interface RedisGeoCommands<K, V> {
	default Long geoadd(K key, V member, Point point) {
		return geoadd(key, null, member, point);
	}

	default Long geoadd(K key, GeoaddOption option, V member, Point point) {
		return geoadd(key, option, Collections.singletonMap(member, point));
	}

	Long geoadd(K key, GeoaddOption option, Map<V, Point> members);

	Double geodist(K key, V member1, V member2, Metric metric);

	@SuppressWarnings("unchecked")
	List<String> geohash(K key, V... members);

	@SuppressWarnings("unchecked")
	List<Point> geopos(K key, V... members);

	Collection<V> georadius(K key, Circle within, GeoRadiusArgs<K> args);

	List<GeoWithin<V>> georadius(K key, Circle within, GeoRadiusWith with, GeoRadiusArgs<K> args);

	List<V> georadiusbymember(K key, V member, Distance distance, GeoRadiusArgs<K> args);

	List<GeoWithin<V>> georadiusbymember(K key, V member, Distance distance, GeoRadiusWith with, GeoRadiusArgs<K> args);
}
