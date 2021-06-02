package scw.redis.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import scw.convert.Converter;
import scw.core.utils.CollectionUtils;
import scw.data.geo.Circle;
import scw.data.geo.Distance;
import scw.data.geo.Marker;
import scw.data.geo.MarkerManager;
import scw.data.geo.Point;
import scw.util.comparator.Sort;

public class RedisMarkerManager<K, V> implements MarkerManager<V> {
	private final RedisCommands<K, V> redisCommands;
	private final K key;

	public RedisMarkerManager(RedisCommands<K, V> redisCommands, K key) {
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
		public scw.data.geo.Marker<V> convert(scw.redis.core.GeoWithin<V> o) {
			return new Marker<V>(o.getMember(), o.getCoordinates());

		};
	};

	@Override
	public List<Marker<V>> getNearbyMarkers(Point point, Distance radius, int count, Sort sort) {
		Collection<GeoWithin<V>> collection = redisCommands.georadius(this.key, new Circle(point, radius),
				new GeoRadiusWith().withCoord(), new GeoRadiusArgs<K>().sort(sort).count(count));
		if (CollectionUtils.isEmpty(collection)) {
			return Collections.emptyList();
		}

		return markerConvert.convert(collection, new ArrayList<Marker<V>>(collection.size()));
	}

}
