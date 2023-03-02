package io.basc.framework.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.basc.framework.util.Range;

@SuppressWarnings("unchecked")
public interface RedisSortedSetsCommands<K, V> {
	List<V> bzpopmin(double timeout, K... keys);

	Long zadd(K key, SetOption setOption, ScoreOption scoreOption, boolean changed, Map<V, Double> memberScores);

	Double zaddIncr(K key, SetOption setOption, ScoreOption scoreOption, boolean changed, double score, V member);

	Long zcard(K key);

	Long zcount(K key, Range<? extends Number> range);

	Long zdiffstore(K destinationKey, K... keys);

	Double zincrby(K key, double increment, V member);

	Collection<V> zinter(InterArgs args, K... keys);

	Collection<Tuple<V>> zinterWithScores(InterArgs args, K... keys);

	Long zinterstore(K destinationKey, InterArgs interArgs, K... keys);

	Long zlexcount(K key, Range<V> range);

	List<Double> zmscore(K key, V... members);

	Collection<Tuple<V>> zpopmax(K key, int count);

	Collection<Tuple<V>> zpopmin(K key, int count);

	Collection<V> zrandmember(K key, int count);

	Collection<Tuple<V>> zrandmemberWithScores(K key, int count);

	Collection<V> zrange(K key, long start, long stop);

	Collection<V> zrangeByLex(K key, Range<V> range, int offset, int limit);

	Collection<V> zrangeByScore(K key, Range<V> range, int offset, int limit);

	Collection<Tuple<V>> zrangeByScoreWithScores(K key, Range<V> range, int offset, int limit);

	Long zrank(K key, V member);

	Long zrem(K key, V... members);

	Long zremrangebylex(K key, Range<V> range);

	Long zremrangebyrank(K key, long start, long stop);

	Long zremrangebyscore(K key, Range<V> range);

	Collection<V> zrevrange(K key, long start, long stop);

	Collection<V> zrevrangebylex(K key, Range<V> range, int offset, int count);

	Collection<V> zrevrangebyscore(K key, Range<V> range, int offset, int count);

	Collection<Tuple<V>> zrevrangebyscoreWithScores(K key, Range<V> range, int offset, int count);

	Long zrevrank(K key, V member);

	Double zscore(K key, V member);

	Collection<V> zunion(InterArgs interArgs, K... keys);

	Collection<Tuple<V>> zunionWithScores(InterArgs interArgs, K... keys);

	Long zunionstore(K destinationKey, InterArgs interArgs, K... keys);
}
