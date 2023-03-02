package io.basc.framework.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.basc.framework.util.Range;

@SuppressWarnings("unchecked")
public interface RedisSortedSetsPipelineCommands<K, V> {
	RedisResponse<List<V>> bzpopmin(double timeout, K... keys);

	RedisResponse<Long> zadd(K key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			Map<V, Double> memberScores);

	RedisResponse<Double> zaddIncr(K key, SetOption setOption, ScoreOption scoreOption, boolean changed, double score,
			V member);

	RedisResponse<Long> zcard(K key);

	RedisResponse<Long> zcount(K key, Range<? extends Number> range);

	RedisResponse<Long> zdiffstore(K destinationKey, K... keys);

	RedisResponse<Double> zincrby(K key, double increment, V member);

	RedisResponse<Collection<V>> zinter(InterArgs args, K... keys);

	RedisResponse<Collection<Tuple<V>>> zinterWithScores(InterArgs args, K... keys);

	RedisResponse<Long> zinterstore(K destinationKey, InterArgs interArgs, K... keys);

	RedisResponse<Long> zlexcount(K key, Range<V> range);

	RedisResponse<List<Double>> zmscore(K key, V... members);

	RedisResponse<Collection<Tuple<V>>> zpopmax(K key, int count);

	RedisResponse<Collection<Tuple<V>>> zpopmin(K key, int count);

	RedisResponse<Collection<V>> zrandmember(K key, int count);

	RedisResponse<Collection<Tuple<V>>> zrandmemberWithScores(K key, int count);

	RedisResponse<Collection<V>> zrange(K key, long start, long stop);

	RedisResponse<Collection<V>> zrangeByLex(K key, Range<V> range, int offset, int limit);

	RedisResponse<Collection<V>> zrangeByScore(K key, Range<V> range, int offset, int limit);

	RedisResponse<Collection<Tuple<V>>> zrangeByScoreWithScores(K key, Range<V> range, int offset, int limit);

	RedisResponse<Long> zrank(K key, V member);

	RedisResponse<Long> zrem(K key, V... members);

	RedisResponse<Long> zremrangebylex(K key, Range<V> range);

	RedisResponse<Long> zremrangebyrank(K key, long start, long stop);

	RedisResponse<Long> zremrangebyscore(K key, Range<V> range);

	RedisResponse<Collection<V>> zrevrange(K key, long start, long stop);

	RedisResponse<Collection<V>> zrevrangebylex(K key, Range<V> range, int offset, int count);

	RedisResponse<Collection<V>> zrevrangebyscore(K key, Range<V> range, int offset, int count);

	RedisResponse<Collection<Tuple<V>>> zrevrangebyscoreWithScores(K key, Range<V> range, int offset, int count);

	RedisResponse<Long> zrevrank(K key, V member);

	RedisResponse<Double> zscore(K key, V member);

	RedisResponse<Collection<V>> zunion(InterArgs interArgs, K... keys);

	RedisResponse<Collection<Tuple<V>>> zunionWithScores(InterArgs interArgs, K... keys);

	RedisResponse<Long> zunionstore(K destinationKey, InterArgs interArgs, K... keys);
}
