package io.basc.framework.redis.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Metric;
import io.basc.framework.data.geo.Point;
import io.basc.framework.redis.GeoRadiusArgs;
import io.basc.framework.redis.GeoRadiusWith;
import io.basc.framework.redis.GeoWithin;
import io.basc.framework.redis.GeoaddOption;
import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisGeoCommands;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisGeoCommands<SK, K, SV, V> extends RedisCodec<SK, K, SV, V>, RedisGeoCommands<K, V> {

	RedisGeoCommands<SK, SV> getSourceRedisGeoCommands();

	@Override
	default Long geoadd(K key, GeoaddOption option, Map<V, Point> members) {
		SK k = getKeyCodec().encode(key);
		Map<SV, Point> tvMap = new LinkedHashMap<SV, Point>(members.size());
		for (Entry<V, Point> entry : members.entrySet()) {
			tvMap.put(getValueCodec().encode(entry.getKey()), entry.getValue());
		}
		return getSourceRedisGeoCommands().geoadd(k, option, tvMap);
	}

	@Override
	default Double geodist(K key, V member1, V member2, Metric metric) {
		SK k = getKeyCodec().encode(key);
		SV v1 = getValueCodec().encode(member1);
		SV v2 = getValueCodec().encode(member2);
		return getSourceRedisGeoCommands().geodist(k, v1, v2, metric);
	}

	@Override
	default List<String> geohash(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] tms = getValueCodec().encode(members);
		return getSourceRedisGeoCommands().geohash(k, tms);
	}

	@Override
	default List<Point> geopos(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] tms = getValueCodec().encode(members);
		return getSourceRedisGeoCommands().geopos(k, tms);
	}

	@Override
	default Collection<V> georadius(K key, Circle within, GeoRadiusArgs<K> args) {
		SK k = getKeyCodec().encode(key);
		GeoRadiusArgs<SK> tArgs = args.convert(getKeyCodec().toEncodeConverter());
		Collection<SV> values = getSourceRedisGeoCommands().georadius(k, within, tArgs);
		return getValueCodec().decode(values);
	}

	@Override
	default List<GeoWithin<V>> georadius(K key, Circle within, GeoRadiusWith with, GeoRadiusArgs<K> args) {
		SK k = getKeyCodec().encode(key);
		GeoRadiusArgs<SK> tArgs = args.convert(getKeyCodec().toEncodeConverter());
		Collection<GeoWithin<SV>> values = getSourceRedisGeoCommands().georadius(k, within, with, tArgs);
		if (values == null) {
			return Collections.emptyList();
		}

		return values.stream().map((e) -> e.convert(getValueCodec().toDecodeConverter())).collect(Collectors.toList());
	}

	@Override
	default List<V> georadiusbymember(K key, V member, Distance distance, GeoRadiusArgs<K> args) {
		SK k = getKeyCodec().encode(key);
		SV tm = getValueCodec().encode(member);
		GeoRadiusArgs<SK> tArgs = args.convert(getKeyCodec().toEncodeConverter());
		List<SV> values = getSourceRedisGeoCommands().georadiusbymember(k, tm, distance, tArgs);
		return getValueCodec().decode(values);
	}

	@Override
	default List<GeoWithin<V>> georadiusbymember(K key, V member, Distance distance, GeoRadiusWith with,
			GeoRadiusArgs<K> args) {
		SK k = getKeyCodec().encode(key);
		SV tm = getValueCodec().encode(member);
		GeoRadiusArgs<SK> tArgs = args.convert(getKeyCodec().toEncodeConverter());
		Collection<GeoWithin<SV>> values = getSourceRedisGeoCommands().georadiusbymember(k, tm, distance, with, tArgs);
		if (values == null) {
			return Collections.emptyList();
		}

		return values.stream().map((e) -> e.convert(getValueCodec().toDecodeConverter())).collect(Collectors.toList());
	}
}
