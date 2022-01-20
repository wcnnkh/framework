package io.basc.framework.redis.convert;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.convert.IdentityConverter;
import io.basc.framework.data.domain.Range;
import io.basc.framework.redis.InterArgs;
import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisSortedSetsPipelineCommands;
import io.basc.framework.redis.ScoreOption;
import io.basc.framework.redis.SetOption;
import io.basc.framework.redis.Tuple;
import io.basc.framework.util.CollectionFactory;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisSortedSetsPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisSortedSetsPipelineCommands<K, V> {

	RedisSortedSetsPipelineCommands<SK, SV> getSourceRedisSortedSetsPipelineCommands();

	@Override
	default RedisResponse<List<V>> bzpopmin(double timeout, K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisSortedSetsPipelineCommands().bzpopmin(timeout, ks).map((vs) -> getValueCodec().decode(vs));
	}

	@Override
	default RedisResponse<Long> zadd(K key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			Map<V, Double> memberScores) {
		SK k = getKeyCodec().encode(key);
		Map<SV, Double> ts = CollectionFactory.convert(memberScores, getValueCodec().toEncodeConverter(),
				new IdentityConverter<Double>());
		return getSourceRedisSortedSetsPipelineCommands().zadd(k, setOption, scoreOption, changed, ts);
	}

	@Override
	default RedisResponse<Double> zaddIncr(K key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			double score, V member) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(member);
		return getSourceRedisSortedSetsPipelineCommands().zaddIncr(k, setOption, scoreOption, changed, score, v);
	}

	@Override
	default RedisResponse<Long> zcard(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands().zcard(k);
	}

	@Override
	default RedisResponse<Long> zcount(K key, Range<? extends Number> range) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands().zcount(k, range);
	}

	@Override
	default RedisResponse<Long> zdiffstore(K destinationKey, K... keys) {
		SK dk = getKeyCodec().encode(destinationKey);
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisSortedSetsPipelineCommands().zdiffstore(dk, ks);
	}

	@Override
	default RedisResponse<Double> zincrby(K key, double increment, V member) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(member);
		return getSourceRedisSortedSetsPipelineCommands().zincrby(k, increment, v);
	}

	@Override
	default RedisResponse<Collection<V>> zinter(InterArgs args, K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisSortedSetsPipelineCommands().zinter(args, ks)
				.map((tvs) -> getValueCodec().toDecodeConverter().convert(tvs, new LinkedHashSet<V>(tvs.size())));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zinterWithScores(InterArgs args, K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisSortedSetsPipelineCommands().zinterWithScores(args, ks)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Long> zinterstore(K destinationKey, InterArgs interArgs, K... keys) {
		SK dk = getKeyCodec().encode(destinationKey);
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisSortedSetsPipelineCommands().zinterstore(dk, interArgs, ks);
	}

	@Override
	default RedisResponse<Long> zlexcount(K key, Range<V> range) {
		SK k = getKeyCodec().encode(key);
		Range<SV> tr = range.convert(getValueCodec().toEncodeConverter());
		return getSourceRedisSortedSetsPipelineCommands().zlexcount(k, tr);
	}

	@Override
	default RedisResponse<List<Double>> zmscore(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] ms = getValueCodec().encode(members);
		return getSourceRedisSortedSetsPipelineCommands().zmscore(k, ms);
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zpopmax(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands().zpopmax(k, count)
				.map((tuples) -> tuples.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zpopmin(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands().zpopmin(k, count)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Collection<V>> zrandmember(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands().zrandmember(k, count)
				.map((tuples) -> getValueCodec().decode(tuples));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zrandmemberWithScores(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands().zrandmemberWithScores(k, count)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Collection<V>> zrange(K key, long start, long stop) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands().zrange(k, start, stop)
				.map((tuples) -> getValueCodec().decode(tuples));
	}

	@Override
	default RedisResponse<Collection<V>> zrangeByLex(K key, Range<V> range, int offset, int limit) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands()
				.zrangeByLex(k, range.convert(getValueCodec().toEncodeConverter()), offset, limit)
				.map((tuples) -> getValueCodec().decode(tuples));
	}

	@Override
	default RedisResponse<Collection<V>> zrangeByScore(K key, Range<V> range, int offset, int limit) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands()
				.zrangeByScore(k, range.convert(getValueCodec().toEncodeConverter()), offset, limit)
				.map((tuples) -> getValueCodec().decode(tuples));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zrangeByScoreWithScores(K key, Range<V> range, int offset, int limit) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsPipelineCommands()
				.zrangeByScoreWithScores(k, range.convert(getValueCodec().toEncodeConverter()), offset, limit)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Long> zrank(K key, V member) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(member);
		return getSourceRedisSortedSetsPipelineCommands().zrank(k, v);
	}

	@Override
	default RedisResponse<Long> zrem(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] ms = getValueCodec().encode(members);
		return getSourceRedisSortedSetsPipelineCommands().zrem(k, ms);
	}

	@Override
	default RedisResponse<Long> zremrangebylex(K key, Range<V> range) {
		return getSourceRedisSortedSetsPipelineCommands().zremrangebylex(getKeyCodec().encode(key),
				range.convert(getValueCodec().toEncodeConverter()));
	}

	@Override
	default RedisResponse<Long> zremrangebyrank(K key, long start, long stop) {
		return getSourceRedisSortedSetsPipelineCommands().zremrangebyrank(getKeyCodec().encode(key), start, stop);
	}

	@Override
	default RedisResponse<Long> zremrangebyscore(K key, Range<V> range) {
		return getSourceRedisSortedSetsPipelineCommands().zremrangebyscore(getKeyCodec().encode(key),
				range.convert(getValueCodec().toEncodeConverter()));
	}

	@Override
	default RedisResponse<Collection<V>> zrevrange(K key, long start, long stop) {
		return getSourceRedisSortedSetsPipelineCommands().zrevrange(getKeyCodec().encode(key), start, stop)
				.map((values) -> getValueCodec().toDecodeConverter().convert(values));
	}

	@Override
	default RedisResponse<Collection<V>> zrevrangebylex(K key, Range<V> range, int offset, int count) {
		return getSourceRedisSortedSetsPipelineCommands().zrevrangebylex(getKeyCodec().encode(key),
				range.convert(getValueCodec().toEncodeConverter()), offset, count)
				.map((values) -> getValueCodec().toDecodeConverter().convert(values));
	}

	@Override
	default RedisResponse<Collection<V>> zrevrangebyscore(K key, Range<V> range, int offset, int count) {
		return getSourceRedisSortedSetsPipelineCommands().zrevrangebyscore(getKeyCodec().encode(key),
				range.convert(getValueCodec().toEncodeConverter()), offset, count)
				.map((values) -> getValueCodec().toDecodeConverter().convert(values));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zrevrangebyscoreWithScores(K key, Range<V> range, int offset,
			int count) {
		return getSourceRedisSortedSetsPipelineCommands()
				.zrevrangebyscoreWithScores(getKeyCodec().encode(key),
						range.convert(getValueCodec().toEncodeConverter()), offset, count)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Long> zrevrank(K key, V member) {
		return getSourceRedisSortedSetsPipelineCommands().zrevrank(getKeyCodec().encode(key),
				getValueCodec().encode(member));
	}

	@Override
	default RedisResponse<Double> zscore(K key, V member) {
		return getSourceRedisSortedSetsPipelineCommands().zscore(getKeyCodec().encode(key),
				getValueCodec().encode(member));
	}

	@Override
	default RedisResponse<Collection<V>> zunion(InterArgs interArgs, K... keys) {
		return getSourceRedisSortedSetsPipelineCommands().zunion(interArgs, getKeyCodec().encode(keys))
				.map((values) -> getValueCodec().toDecodeConverter().convert(values));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zunionWithScores(InterArgs interArgs, K... keys) {
		return getSourceRedisSortedSetsPipelineCommands().zunionWithScores(interArgs, getKeyCodec().encode(keys))
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Long> zunionstore(K destinationKey, InterArgs interArgs, K... keys) {
		return getSourceRedisSortedSetsPipelineCommands().zunionstore(getKeyCodec().encode(destinationKey), interArgs,
				getKeyCodec().encode(keys));
	}
}
