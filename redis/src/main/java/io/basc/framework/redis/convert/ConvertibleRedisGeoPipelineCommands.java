package io.basc.framework.redis.convert;

import java.util.Collection;
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
import io.basc.framework.redis.RedisGeoPipelineCommands;
import io.basc.framework.redis.RedisResponse;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisGeoPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisGeoPipelineCommands<K, V> {
	RedisGeoPipelineCommands<SK, SV> getSourceRedisGeoPipelineCommands();

	@Override
	default RedisResponse<Long> geoadd(K key, GeoaddOption option, Map<V, Point> members) {
		SK k = getKeyCodec().encode(key);
		Map<SV, Point> tvMap = new LinkedHashMap<SV, Point>(members.size());
		for (Entry<V, Point> entry : members.entrySet()) {
			tvMap.put(getValueCodec().encode(entry.getKey()), entry.getValue());
		}
		return getSourceRedisGeoPipelineCommands().geoadd(k, option, tvMap);
	}

	@Override
	default RedisResponse<Double> geodist(K key, V member1, V member2, Metric metric) {
		SK k = getKeyCodec().encode(key);
		SV v1 = getValueCodec().encode(member1);
		SV v2 = getValueCodec().encode(member2);
		return getSourceRedisGeoPipelineCommands().geodist(k, v1, v2, metric);
	}

	@Override
	default RedisResponse<List<String>> geohash(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] tms = getValueCodec().encode(members);
		return getSourceRedisGeoPipelineCommands().geohash(k, tms);
	}

	@Override
	default RedisResponse<List<Point>> geopos(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] tms = getValueCodec().encode(members);
		return getSourceRedisGeoPipelineCommands().geopos(k, tms);
	}

	@Override
	default RedisResponse<Collection<V>> georadius(K key, Circle within, GeoRadiusArgs<K> args) {
		SK k = getKeyCodec().encode(key);
		GeoRadiusArgs<SK> tArgs = args.convert(getKeyCodec().toEncodeConverter());
		return getSourceRedisGeoPipelineCommands().georadius(k, within, tArgs)
				.map((values) -> getValueCodec().decode(values));
	}

	@Override
	default RedisResponse<List<GeoWithin<V>>> georadius(K key, Circle within, GeoRadiusWith with,
			GeoRadiusArgs<K> args) {
		SK k = getKeyCodec().encode(key);
		GeoRadiusArgs<SK> tArgs = args.convert(getKeyCodec().toEncodeConverter());
		return getSourceRedisGeoPipelineCommands().georadius(k, within, with, tArgs).map((values) -> values.stream()
				.map((e) -> e.convert(getValueCodec().toDecodeConverter())).collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<List<V>> georadiusbymember(K key, V member, Distance distance, GeoRadiusArgs<K> args) {
		SK k = getKeyCodec().encode(key);
		SV tm = getValueCodec().encode(member);
		GeoRadiusArgs<SK> tArgs = args.convert(getKeyCodec().toEncodeConverter());
		return getSourceRedisGeoPipelineCommands().georadiusbymember(k, tm, distance, tArgs)
				.map((values) -> getValueCodec().decode(values));
	}

	@Override
	default RedisResponse<List<GeoWithin<V>>> georadiusbymember(K key, V member, Distance distance, GeoRadiusWith with,
			GeoRadiusArgs<K> args) {
		SK k = getKeyCodec().encode(key);
		SV tm = getValueCodec().encode(member);
		GeoRadiusArgs<SK> tArgs = args.convert(getKeyCodec().toEncodeConverter());
		return getSourceRedisGeoPipelineCommands().georadiusbymember(k, tm, distance, with, tArgs)
				.map((values) -> values.stream().map((e) -> e.convert(getValueCodec().toDecodeConverter()))
						.collect(Collectors.toList()));
	}
}
