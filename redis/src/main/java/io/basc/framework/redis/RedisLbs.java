package io.basc.framework.redis;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Lbs;
import io.basc.framework.data.geo.Marker;
import io.basc.framework.data.geo.Point;
import io.basc.framework.util.collection.CollectionUtils;
import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.comparator.Sort;

@SuppressWarnings("unchecked")
public class RedisLbs<K, V> implements Lbs<V> {
	private final RedisClient<K, V> factory;
	private final K key;

	public RedisLbs(RedisClient<K, V> factory, K key) {
		this.factory = factory;
		this.key = key;
	}

	@Override
	public void report(Marker<V> marker) {
		factory.geoadd(key, marker.getName(), marker);
	}

	@Override
	public Marker<V> getMarker(V key) {
		List<Point> list = factory.geopos(this.key, key);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return new Marker<V>(key, list.get(0));
	}

	@Override
	public boolean remove(V key) {
		// redis geo 本质上是一个有序set
		return factory.zrem(this.key, key) == 1;
	}

	@Override
	public boolean exists(V key) {
		return factory.zrank(this.key, key) != null;
	}

	protected final Function<GeoWithin<V>, Marker<V>> markerConvert = new Function<GeoWithin<V>, Marker<V>>() {
		public io.basc.framework.data.geo.Marker<V> apply(io.basc.framework.redis.GeoWithin<V> o) {
			return new Marker<V>(o.getMember(), o.getCoordinates());
		};
	};

	@Override
	public Elements<Marker<V>> getNearbyMarkers(Point point, Distance radius, int count, Sort sort) {
		Collection<GeoWithin<V>> collection = factory.georadius(this.key, new Circle(point, radius),
				new GeoRadiusWith().withCoord(), new GeoRadiusArgs<K>().sort(sort).count(count));
		if (CollectionUtils.isEmpty(collection)) {
			return Elements.empty();
		}

		return Elements.of(() -> collection.stream().map(markerConvert));
	}

}
