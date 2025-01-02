package io.basc.framework.redis.convert;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.core.convert.transform.stractegy.CollectionFactory;
import io.basc.framework.redis.InterArgs;
import io.basc.framework.redis.RedisSortedSetsCommands;
import io.basc.framework.redis.ScoreOption;
import io.basc.framework.redis.SetOption;
import io.basc.framework.redis.Tuple;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Function;
import io.basc.framework.util.Range;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisSortedSetsCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisSortedSetsCommands<K, V> {

	RedisSortedSetsCommands<SK, SV> getSourceRedisSortedSetsCommands();

	@Override
	default List<V> bzpopmin(double timeout, K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		List<SV> vs = getSourceRedisSortedSetsCommands().bzpopmin(timeout, ks);
		return getValueCodec().decodeAll(vs);
	}

	@Override
	default Long zadd(K key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			Map<V, Double> memberScores) {
		SK k = getKeyCodec().encode(key);
		Map<SV, Double> ts = CollectionFactory.convert(memberScores, getValueCodec().toEncodeProcessor(),
				Function.identity());
		return getSourceRedisSortedSetsCommands().zadd(k, setOption, scoreOption, changed, ts);
	}

	@Override
	default Double zaddIncr(K key, SetOption setOption, ScoreOption scoreOption, boolean changed, double score,
			V member) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(member);
		return getSourceRedisSortedSetsCommands().zaddIncr(k, setOption, scoreOption, changed, score, v);
	}

	@Override
	default Long zcard(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands().zcard(k);
	}

	@Override
	default Long zcount(K key, Range<? extends Number> range) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisSortedSetsCommands().zcount(k, range);
	}

	@Override
	default Long zdiffstore(K destinationKey, K... keys) {
		SK dk = getKeyCodec().encode(destinationKey);
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisSortedSetsCommands().zdiffstore(dk, ks);
	}

	@Override
	default Double zincrby(K key, double increment, V member) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(member);
		return getSourceRedisSortedSetsCommands().zincrby(k, increment, v);
	}

	@Override
	default Collection<V> zinter(InterArgs args, K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		Collection<SV> tvs = getSourceRedisSortedSetsCommands().zinter(args, ks);
		if (CollectionUtils.isEmpty(tvs)) {
			return Collections.emptyList();
		}
		return getValueCodec().toDecodeProcessor().processAll(tvs, new LinkedHashSet<V>(tvs.size()));
	}

	@Override
	default Collection<Tuple<V>> zinterWithScores(InterArgs args, K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		Collection<Tuple<SV>> values = getSourceRedisSortedSetsCommands().zinterWithScores(args, ks);
		if (values == null) {
			return Collections.emptyList();
		}

		return values.stream().map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
				.collect(Collectors.toList());
	}

	@Override
	default Long zinterstore(K destinationKey, InterArgs interArgs, K... keys) {
		SK dk = getKeyCodec().encode(destinationKey);
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisSortedSetsCommands().zinterstore(dk, interArgs, ks);
	}

	@Override
	default Long zlexcount(K key, Range<V> range) {
		SK k = getKeyCodec().encode(key);
		Range<SV> tr = range.convert(getValueCodec()::encode);
		return getSourceRedisSortedSetsCommands().zlexcount(k, tr);
	}

	@Override
	default List<Double> zmscore(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] ms = getValueCodec().encodeAll(members);
		return getSourceRedisSortedSetsCommands().zmscore(k, ms);
	}

	@Override
	default Collection<Tuple<V>> zpopmax(K key, int count) {
		SK k = getKeyCodec().encode(key);
		Collection<Tuple<SV>> tuples = getSourceRedisSortedSetsCommands().zpopmax(k, count);
		if (tuples == null) {
			return Collections.emptyList();
		}

		return tuples.stream().map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
				.collect(Collectors.toList());
	}

	@Override
	default Collection<Tuple<V>> zpopmin(K key, int count) {
		SK k = getKeyCodec().encode(key);
		Collection<Tuple<SV>> values = getSourceRedisSortedSetsCommands().zpopmin(k, count);
		if (values == null) {
			return Collections.emptyList();
		}

		return values.stream().map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
				.collect(Collectors.toList());
	}

	@Override
	default Collection<V> zrandmember(K key, int count) {
		SK k = getKeyCodec().encode(key);
		Collection<SV> tuples = getSourceRedisSortedSetsCommands().zrandmember(k, count);
		return getValueCodec().decodeAll(tuples);
	}

	@Override
	default Collection<Tuple<V>> zrandmemberWithScores(K key, int count) {
		SK k = getKeyCodec().encode(key);
		Collection<Tuple<SV>> values = getSourceRedisSortedSetsCommands().zrandmemberWithScores(k, count);
		if (values == null) {
			return Collections.emptyList();
		}

		return values.stream().map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
				.collect(Collectors.toList());
	}

	@Override
	default Collection<V> zrange(K key, long start, long stop) {
		SK k = getKeyCodec().encode(key);
		Collection<SV> tuples = getSourceRedisSortedSetsCommands().zrange(k, start, stop);
		return getValueCodec().decodeAll(tuples);
	}

	@Override
	default Collection<V> zrangeByLex(K key, Range<V> range, int offset, int limit) {
		SK k = getKeyCodec().encode(key);
		Collection<SV> tuples = getSourceRedisSortedSetsCommands().zrangeByLex(k,
				range.convert(getValueCodec()::encode), offset, limit);
		return getValueCodec().decodeAll(tuples);
	}

	@Override
	default Collection<V> zrangeByScore(K key, Range<V> range, int offset, int limit) {
		SK k = getKeyCodec().encode(key);
		Collection<SV> tuples = getSourceRedisSortedSetsCommands().zrangeByScore(k,
				range.convert(getValueCodec()::encode), offset, limit);
		return getValueCodec().decodeAll(tuples);
	}

	@Override
	default Collection<Tuple<V>> zrangeByScoreWithScores(K key, Range<V> range, int offset, int limit) {
		SK k = getKeyCodec().encode(key);
		Collection<Tuple<SV>> values = getSourceRedisSortedSetsCommands().zrangeByScoreWithScores(k,
				range.convert(getValueCodec()::encode), offset, limit);
		if (values == null) {
			return Collections.emptyList();
		}

		return values.stream().map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
				.collect(Collectors.toList());
	}

	@Override
	default Long zrank(K key, V member) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(member);
		return getSourceRedisSortedSetsCommands().zrank(k, v);
	}

	@Override
	default Long zrem(K key, V... members) {
		SK k = getKeyCodec().encode(key);
		SV[] ms = getValueCodec().encodeAll(members);
		return getSourceRedisSortedSetsCommands().zrem(k, ms);
	}

	@Override
	default Long zremrangebylex(K key, Range<V> range) {
		return getSourceRedisSortedSetsCommands().zremrangebylex(getKeyCodec().encode(key),
				range.convert(getValueCodec()::encode));
	}

	@Override
	default Long zremrangebyrank(K key, long start, long stop) {
		return getSourceRedisSortedSetsCommands().zremrangebyrank(getKeyCodec().encode(key), start, stop);
	}

	@Override
	default Long zremrangebyscore(K key, Range<V> range) {
		return getSourceRedisSortedSetsCommands().zremrangebyscore(getKeyCodec().encode(key),
				range.convert(getValueCodec()::encode));
	}

	@Override
	default Collection<V> zrevrange(K key, long start, long stop) {
		Collection<SV> values = getSourceRedisSortedSetsCommands().zrevrange(getKeyCodec().encode(key), start, stop);
		return getValueCodec().toDecodeProcessor().processAll(values);
	}

	@Override
	default Collection<V> zrevrangebylex(K key, Range<V> range, int offset, int count) {
		Collection<SV> values = getSourceRedisSortedSetsCommands().zrevrangebylex(getKeyCodec().encode(key),
				range.convert(getValueCodec()::encode), offset, count);
		return getValueCodec().toDecodeProcessor().processAll(values);
	}

	@Override
	default Collection<V> zrevrangebyscore(K key, Range<V> range, int offset, int count) {
		Collection<SV> values = getSourceRedisSortedSetsCommands().zrevrangebyscore(getKeyCodec().encode(key),
				range.convert(getValueCodec()::encode), offset, count);
		return getValueCodec().toDecodeProcessor().processAll(values);
	}

	@Override
	default Collection<Tuple<V>> zrevrangebyscoreWithScores(K key, Range<V> range, int offset, int count) {
		Collection<Tuple<SV>> values = getSourceRedisSortedSetsCommands().zrevrangebyscoreWithScores(
				getKeyCodec().encode(key), range.convert(getValueCodec()::encode), offset, count);
		if (values == null) {
			return Collections.emptyList();
		}

		return values.stream().map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
				.collect(Collectors.toList());
	}

	@Override
	default Long zrevrank(K key, V member) {
		return getSourceRedisSortedSetsCommands().zrevrank(getKeyCodec().encode(key), getValueCodec().encode(member));
	}

	@Override
	default Double zscore(K key, V member) {
		return getSourceRedisSortedSetsCommands().zscore(getKeyCodec().encode(key), getValueCodec().encode(member));
	}

	@Override
	default Collection<V> zunion(InterArgs interArgs, K... keys) {
		Collection<SV> values = getSourceRedisSortedSetsCommands().zunion(interArgs, getKeyCodec().encodeAll(keys));
		return getValueCodec().toDecodeProcessor().processAll(values);
	}

	@Override
	default Collection<Tuple<V>> zunionWithScores(InterArgs interArgs, K... keys) {
		Collection<Tuple<SV>> values = getSourceRedisSortedSetsCommands().zunionWithScores(interArgs,
				getKeyCodec().encodeAll(keys));
		if (values == null) {
			return Collections.emptyList();
		}

		return values.stream().map((o) -> new Tuple<V>(getValueCodec().decode(o.getValue()), o.getScore()))
				.collect(Collectors.toList());
	}

	@Override
	default Long zunionstore(K destinationKey, InterArgs interArgs, K... keys) {
		return getSourceRedisSortedSetsCommands().zunionstore(getKeyCodec().encode(destinationKey), interArgs,
				getKeyCodec().encodeAll(keys));
	}
}
