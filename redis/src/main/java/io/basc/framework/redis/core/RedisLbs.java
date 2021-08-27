package io.basc.framework.redis.core;

import io.basc.framework.convert.Converter;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Lbs;
import io.basc.framework.data.geo.Marker;
import io.basc.framework.data.geo.Point;
import io.basc.framework.util.comparator.Sort;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class RedisLbs<K, V> implements Lbs<V> {
	private final RedisCommands<K, V> redisCommands;
	private final K key;

	public RedisLbs(RedisCommands<K, V> redisCommands, K key) {
		this.redisCommands = redisCommands;
		this.key = key;
	}

	@Override
	public void report(Marker<V> marker) {
		redisCommands.geoadd(key, marker.getName(), marker);
	}

	@Override
	public Marker<V> getMarker(V key) {
		List<Point> list = redisCommands.geopos(this.key, key);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return new Marker<V>(key, list.get(0));
	}

	@Override
	public boolean remove(V key) {
		// redis geo 本质上是一个有序set
		return redisCommands.zrem(this.key, key) == 1;
	}

	@Override
	public boolean exists(V key) {
		return redisCommands.zrank(this.key, key) != null;
	}

	protected final Converter<GeoWithin<V>, Marker<V>> markerConvert = new Converter<GeoWithin<V>, Marker<V>>() {
		public io.basc.framework.data.geo.Marker<V> convert(io.basc.framework.redis.core.GeoWithin<V> o) {
			return new Marker<V>(o.getMember(), o.getCoordinates());

		};
	};

	@Override
	public Cursor<Marker<V>> getNearbyMarkers(Point point, Distance radius, int count, Sort sort) {
		Collection<GeoWithin<V>> collection = redisCommands.georadius(this.key, new Circle(point, radius),
				new GeoRadiusWith().withCoord(), new GeoRadiusArgs<K>().sort(sort).count(count));
		if (CollectionUtils.isEmpty(collection)) {
			return StreamProcessorSupport.emptyCursor();
		}

		return StreamProcessorSupport
				.cursor(markerConvert.convert(collection, new ArrayList<Marker<V>>(collection.size())).stream());
	}

}
