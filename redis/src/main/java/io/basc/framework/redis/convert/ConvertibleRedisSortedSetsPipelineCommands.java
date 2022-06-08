package io.basc.framework.redis.convert;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.data.domain.Range;
import io.basc.framework.redis.InterArgs;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisSortedSetsPipelineCommands;
import io.basc.framework.redis.ScoreOption;
import io.basc.framework.redis.SetOption;
import io.basc.framework.redis.Tuple;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.stream.Processor;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisSortedSetsPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisSortedSetsPipelineCommands<K, V> {

	RedisSortedSetsPipelineCommands<SK, SV> getSourceRedisSortedSetsCommands();

	@Override
	default RedisResponse<List<V>> bzpopmin(double timeout, K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisSortedSetsCommands().bzpopmin(timeout, ks).map((vs) -> getValueCodec().decodeAll(vs));
	}

	@Override
	default RedisResponse<Long> zadd(K key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			Map<V, Double> memberScores) {
		SK k = getKeyCodec().encode(key);
		Map<SV, Double> ts = CollectionFactory.convert(memberScores, getValueCodec().toEncodeProcessor(),
				Processor.identity());
		return getSourceRedisSortedSetsCommands().zadd(k, setOption, scoreOption, changed, ts);
	}

	@Override
	default RedisResponse<Double> zaddIncr(K key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			double score, V member) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(member);
		return getSourceRedisSortedSetsCommands().zaddIncr(k, setOption, scoreOption, changed, score, v);
	}

	@Override
	default RedisResponse<Long> zcard(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands().zcard(k);
	}

	@Override
	default RedisResponse<Long> zcount(K key, Range<? extends Number> range) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands().zcount(k, range);
	}

	@Override
	default RedisResponse<Long> zdiffstore(K destinationKey, K... keys) {
		SK dk = getKeyCodec().encode(destinationKey);
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisSortedSetsCommands().zdiffstore(dk, ks);
	}

	@Override
	default RedisResponse<Double> zincrby(K key, double increment, V member) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(member);
		return getSourceRedisSortedSetsCommands().zincrby(k, increment, v);
	}

	@Override
	default RedisResponse<Collection<V>> zinter(InterArgs args, K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisSortedSetsCommands().zinter(args, ks)
				.map((tvs) -> getValueCodec().toDecodeProcessor().processTo(tvs, new LinkedHashSet<V>(tvs.size())));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zinterWithScores(InterArgs args, K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisSortedSetsCommands().zinterWithScores(args, ks)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Long> zinterstore(K destinationKey, InterArgs interArgs, K... keys) {
		SK dk = getKeyCodec().encode(destinationKey);
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisSortedSetsCommands().zinterstore(dk, interArgs, ks);
	}

	@Override
	default RedisResponse<Long> zlexcount(K key, Range<V> range) {
		SK k = getKeyCodec().encode(key);
		Range<SV> tr = range.convert(getValueCodec().toEncodeProcessor());
		return getSourceRedisSortedSetsCommands().zlexcount(k, tr);
	}

	@Override
	default RedisResponse<List<Double>> zmscore(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] ms = getValueCodec().encodeAll(members);
		return getSourceRedisSortedSetsCommands().zmscore(k, ms);
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zpopmax(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands().zpopmax(k, count)
				.map((tuples) -> tuples.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zpopmin(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands().zpopmin(k, count)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Collection<V>> zrandmember(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands().zrandmember(k, count)
				.map((tuples) -> getValueCodec().decodeAll(tuples));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zrandmemberWithScores(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands().zrandmemberWithScores(k, count)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Collection<V>> zrange(K key, long start, long stop) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands().zrange(k, start, stop)
				.map((tuples) -> getValueCodec().decodeAll(tuples));
	}

	@Override
	default RedisResponse<Collection<V>> zrangeByLex(K key, Range<V> range, int offset, int limit) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands()
				.zrangeByLex(k, range.convert(getValueCodec().toEncodeProcessor()), offset, limit)
				.map((tuples) -> getValueCodec().decodeAll(tuples));
	}

	@Override
	default RedisResponse<Collection<V>> zrangeByScore(K key, Range<V> range, int offset, int limit) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands()
				.zrangeByScore(k, range.convert(getValueCodec().toEncodeProcessor()), offset, limit)
				.map((tuples) -> getValueCodec().decodeAll(tuples));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zrangeByScoreWithScores(K key, Range<V> range, int offset, int limit) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands()
				.zrangeByScoreWithScores(k, range.convert(getValueCodec().toEncodeProcessor()), offset, limit)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Long> zrank(K key, V member) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(member);
		return getSourceRedisSortedSetsCommands().zrank(k, v);
	}

	@Override
	default RedisResponse<Long> zrem(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] ms = getValueCodec().encodeAll(members);
		return getSourceRedisSortedSetsCommands().zrem(k, ms);
	}

	@Override
	default RedisResponse<Long> zremrangebylex(K key, Range<V> range) {
		return getSourceRedisSortedSetsCommands().zremrangebylex(getKeyCodec().encode(key),
				range.convert(getValueCodec().toEncodeProcessor()));
	}

	@Override
	default RedisResponse<Long> zremrangebyrank(K key, long start, long stop) {
		return getSourceRedisSortedSetsCommands().zremrangebyrank(getKeyCodec().encode(key), start, stop);
	}

	@Override
	default RedisResponse<Long> zremrangebyscore(K key, Range<V> range) {
		return getSourceRedisSortedSetsCommands().zremrangebyscore(getKeyCodec().encode(key),
				range.convert(getValueCodec().toEncodeProcessor()));
	}

	@Override
	default RedisResponse<Collection<V>> zrevrange(K key, long start, long stop) {
		return getSourceRedisSortedSetsCommands().zrevrange(getKeyCodec().encode(key), start, stop)
				.map((values) -> getValueCodec().toDecodeProcessor().processAll(values));
	}

	@Override
	default RedisResponse<Collection<V>> zrevrangebylex(K key, Range<V> range, int offset, int count) {
		return getSourceRedisSortedSetsCommands().zrevrangebylex(getKeyCodec().encode(key),
				range.convert(getValueCodec().toEncodeProcessor()), offset, count)
				.map((values) -> getValueCodec().toDecodeProcessor().processAll(values));
	}

	@Override
	default RedisResponse<Collection<V>> zrevrangebyscore(K key, Range<V> range, int offset, int count) {
		return getSourceRedisSortedSetsCommands().zrevrangebyscore(getKeyCodec().encode(key),
				range.convert(getValueCodec().toEncodeProcessor()), offset, count)
				.map((values) -> getValueCodec().toDecodeProcessor().processAll(values));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zrevrangebyscoreWithScores(K key, Range<V> range, int offset,
			int count) {
		return getSourceRedisSortedSetsCommands()
				.zrevrangebyscoreWithScores(getKeyCodec().encode(key),
						range.convert(getValueCodec().toEncodeProcessor()), offset, count)
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Long> zrevrank(K key, V member) {
		return getSourceRedisSortedSetsCommands().zrevrank(getKeyCodec().encode(key), getValueCodec().encode(member));
	}

	@Override
	default RedisResponse<Double> zscore(K key, V member) {
		return getSourceRedisSortedSetsCommands().zscore(getKeyCodec().encode(key), getValueCodec().encode(member));
	}

	@Override
	default RedisResponse<Collection<V>> zunion(InterArgs interArgs, K... keys) {
		return getSourceRedisSortedSetsCommands().zunion(interArgs, getKeyCodec().encodeAll(keys))
				.map((values) -> getValueCodec().toDecodeProcessor().processAll(values));
	}

	@Override
	default RedisResponse<Collection<Tuple<V>>> zunionWithScores(InterArgs interArgs, K... keys) {
		return getSourceRedisSortedSetsCommands().zunionWithScores(interArgs, getKeyCodec().encodeAll(keys))
				.map((values) -> values.stream()
						.map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
						.collect(Collectors.toList()));
	}

	@Override
	default RedisResponse<Long> zunionstore(K destinationKey, InterArgs interArgs, K... keys) {
		return getSourceRedisSortedSetsCommands().zunionstore(getKeyCodec().encode(destinationKey), interArgs,
				getKeyCodec().encodeAll(keys));
	}
}
