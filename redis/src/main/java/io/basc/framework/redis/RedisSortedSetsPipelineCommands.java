package io.basc.framework.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.basc.framework.data.domain.Range;

@SuppressWarnings("unchecked")
public interface RedisSortedSetsPipelineCommands<K, V> {
	/**
	 * https://redis.io/commands/bzpopmin<br/>
	 * <br/>
	 * 
	 * Available since 5.0.0.
	 * 
	 * Time complexity: O(log(N)) with N being the number of elements in the sorted
	 * set.
	 * 
	 * BZPOPMIN is the blocking variant of the sorted set ZPOPMIN primitive.
	 * 
	 * It is the blocking version because it blocks the connection when there are no
	 * members to pop from any of the given sorted sets. A member with the lowest
	 * score is popped from first sorted set that is non-empty, with the given keys
	 * being checked in the order that they are given.
	 * 
	 * The timeout argument is interpreted as an double value specifying the maximum
	 * number of seconds to block. A timeout of zero can be used to block
	 * indefinitely.
	 * 
	 * See the BLPOP documentation for the exact semantics, since BZPOPMIN is
	 * identical to BLPOP with the only difference being the data structure being
	 * popped from.
	 * 
	 * @param timeout >= 6.0: timeout is interpreted as a double instead of an
	 *                integer.
	 * @param keys
	 * @return Array reply: specifically:
	 * 
	 *         A nil multi-bulk when no element could be popped and the timeout
	 *         expired. A three-element multi-bulk with the first element being the
	 *         name of the key where a member was popped, the second element is the
	 *         popped member itself, and the third element is the score of the
	 *         popped element.
	 */
	RedisResponse<List<V>> bzpopmin(double timeout, K... keys);

	/**
	 * https://redis.io/commands/zadd<br/>
	 * <br/>
	 * 
	 * @return When used without optional arguments, the number of elements added to
	 *         the sorted set (excluding score updates). If the CH option is
	 *         specified, the number of elements that were changed (added or
	 *         updated). If the INCR option is specified, the return value will be
	 *         Bulk string reply:
	 * 
	 *         The new score of member (a double precision floating point number)
	 *         represented as string, or nil if the operation was aborted (when
	 *         called with either the XX or the NX option).
	 */
	RedisResponse<Long> zadd(K key, SetOption setOption, ScoreOption scoreOption, boolean changed,
			Map<V, Double> memberScores);

	RedisResponse<Double> zaddIncr(K key, SetOption setOption, ScoreOption scoreOption, boolean changed, double score,
			V member);

	/**
	 * https://redis.io/commands/zcard<br/>
	 * <br/>
	 * Returns the sorted set cardinality (number of elements) of the sorted set
	 * stored at key.
	 * 
	 * @param key
	 * @return Integer reply: the cardinality (number of elements) of the sorted
	 *         set, or 0 if key does not exist.
	 */
	RedisResponse<Long> zcard(K key);

	/**
	 * https://redis.io/commands/zcount<br/>
	 * <br/>
	 * Returns the number of elements in the sorted set at key with a score between
	 * min and max.
	 * 
	 * The min and max arguments have the same semantic as described for
	 * ZRANGEBYSCORE.
	 * 
	 * Note: the command has a complexity of just O(log(N)) because it uses elements
	 * ranks (see ZRANK) to get an idea of the range. Because of this there is no
	 * need to do a work proportional to the size of the range.
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return Integer reply: the number of elements in the specified score range.
	 */
	RedisResponse<Long> zcount(K key, Range<? extends Number> range);

	/**
	 * https://redis.io/commands/zdiffstore<br/>
	 * <br/>
	 * Computes the difference between the first and all successive input sorted
	 * sets and stores the result in destination. The total number of input keys is
	 * specified by numkeys.
	 * 
	 * Keys that do not exist are considered to be empty sets.
	 * 
	 * If destination already exists, it is overwritten.
	 * 
	 * @param destination
	 * @param numKeys
	 * @param keys
	 * @return Integer reply: the number of elements in the resulting sorted set at
	 *         destination.
	 */
	RedisResponse<Long> zdiffstore(K destinationKey, K... keys);

	RedisResponse<Double> zincrby(K key, double increment, V member);

	/**
	 * https://redis.io/commands/zinter<br/>
	 * <br/>
	 * This command is similar to ZINTERSTORE, but instead of storing the resulting
	 * sorted set, it is returned to the client.
	 * 
	 * For a description of the WEIGHTS and AGGREGATE options, see ZUNIONSTORE.
	 * 
	 * @return Array reply: the result of intersection (optionally with their
	 *         scores, in case the WITHSCORES option is given).
	 */
	RedisResponse<Collection<V>> zinter(InterArgs args, K... keys);

	RedisResponse<Collection<Tuple<V>>> zinterWithScores(InterArgs args, K... keys);

	/**
	 * https://redis.io/commands/zinterstore<br/>
	 * <br/>
	 * Computes the intersection of numkeys sorted sets given by the specified keys,
	 * and stores the result in destination. It is mandatory to provide the number
	 * of input keys (numkeys) before passing the input keys and the other
	 * (optional) arguments.
	 * 
	 * By default, the resulting score of an element is the sum of its scores in the
	 * sorted sets where it exists. Because intersection requires an element to be a
	 * member of every given sorted set, this results in the score of every element
	 * in the resulting sorted set to be equal to the number of input sorted sets.
	 * 
	 * For a description of the WEIGHTS and AGGREGATE options, see ZUNIONSTORE.
	 * 
	 * If destination already exists, it is overwritten.
	 * 
	 * @return Integer reply: the number of elements in the resulting sorted set at
	 *         destination.
	 */
	RedisResponse<Long> zinterstore(K destinationKey, InterArgs interArgs, K... keys);

	/**
	 * https://redis.io/commands/zlexcount<br/>
	 * <br/>
	 * When all the elements in a sorted set are inserted with the same score, in
	 * order to force lexicographical ordering, this command returns the number of
	 * elements in the sorted set at key with a value between min and max.
	 * 
	 * The min and max arguments have the same meaning as described for ZRANGEBYLEX.
	 * 
	 * Note: the command has a complexity of just O(log(N)) because it uses elements
	 * ranks (see ZRANK) to get an idea of the range. Because of this there is no
	 * need to do a work proportional to the size of the range.
	 * 
	 * @return Integer reply: the number of elements in the specified score range.
	 */
	RedisResponse<Long> zlexcount(K key, Range<V> range);

	/**
	 * https://redis.io/commands/zmscore<br/>
	 * <br/>
	 * Returns the scores associated with the specified members in the sorted set
	 * stored at key.
	 * 
	 * For every member that does not exist in the sorted set, a nil value is
	 * returned.
	 * 
	 * @param key
	 * @param members
	 * @return Array reply: list of scores or nil associated with the specified
	 *         member values (a double precision floating point number), represented
	 *         as strings.
	 */
	RedisResponse<List<Double>> zmscore(K key, V... members);

	/**
	 * https://redis.io/commands/zpopmax<br/>
	 * <br/>
	 * 
	 * Removes and returns up to count members with the highest scores in the sorted
	 * set stored at key.
	 * 
	 * When left unspecified, the default value for count is 1. Specifying a count
	 * value that is higher than the sorted set's cardinality will not produce an
	 * error. When returning multiple elements, the one with the highest score will
	 * be the first, followed by the elements with lower scores.
	 * 
	 * @param key
	 * @param count
	 * @return Array reply: list of popped elements and scores.
	 */
	RedisResponse<Collection<Tuple<V>>> zpopmax(K key, int count);

	/**
	 * https://redis.io/commands/zpopmin <br/>
	 * <br/>
	 * Removes and returns up to count members with the lowest scores in the sorted
	 * set stored at key.
	 * 
	 * When left unspecified, the default value for count is 1. Specifying a count
	 * value that is higher than the sorted set's cardinality will not produce an
	 * error. When returning multiple elements, the one with the lowest score will
	 * be the first, followed by the elements with greater scores.
	 * 
	 * @param key
	 * @param count
	 * @return Array reply: list of popped elements and scores.
	 */
	RedisResponse<Collection<Tuple<V>>> zpopmin(K key, int count);

	/**
	 * https://redis.io/commands/zrandmember<br/>
	 * <br/>
	 * 
	 * When called with just the key argument, return a random element from the
	 * sorted set value stored at key.
	 * 
	 * If the provided count argument is positive, return an array of distinct
	 * elements. The array's length is either count or the sorted set's cardinality
	 * (ZCARD), whichever is lower.
	 * 
	 * If called with a negative count, the behavior changes and the command is
	 * allowed to return the same element multiple times. In this case, the number
	 * of returned elements is the absolute value of the specified count.
	 * 
	 * The optional WITHSCORES modifier changes the reply so it includes the
	 * respective scores of the randomly selected elements from the sorted set.
	 * 
	 * @return Bulk string reply: without the additional count argument, the command
	 *         returns a Bulk Reply with the randomly selected element, or nil when
	 *         key does not exist.
	 * 
	 *         Array reply: when the additional count argument is passed, the
	 *         command returns an array of elements, or an empty array when key does
	 *         not exist. If the WITHSCORES modifier is used, the reply is a list
	 *         elements and their scores from the sorted set.
	 */
	RedisResponse<Collection<V>> zrandmember(K key, int count);

	RedisResponse<Collection<Tuple<V>>> zrandmemberWithScores(K key, int count);

	/**
	 * https://redis.io/commands/zrange<br/>
	 * <br/>
	 * 
	 * <br/>
	 * <br/>
	 * >= 6.2: Added the REV, BYSCORE, BYLEX and LIMIT options.
	 * 
	 * @return Array reply: list of elements in the specified range (optionally with
	 *         their scores, in case the WITHSCORES option is given).
	 */
	RedisResponse<Collection<V>> zrange(K key, long start, long stop);

	RedisResponse<Collection<V>> zrangeByLex(K key, Range<V> range, int offset, int limit);

	RedisResponse<Collection<V>> zrangeByScore(K key, Range<V> range, int offset, int limit);

	RedisResponse<Collection<Tuple<V>>> zrangeByScoreWithScores(K key, Range<V> range, int offset, int limit);

	/**
	 * https://redis.io/commands/zrank<br/>
	 * <br/>
	 * 
	 * @param key
	 * @param member
	 * @return If member exists in the sorted set, Integer reply: the rank of
	 *         member. If member does not exist in the sorted set or key does not
	 *         exist, Bulk string reply: nil.
	 */
	RedisResponse<Long> zrank(K key, V member);

	/**
	 * https://redis.io/commands/zrem<br/>
	 * <br/>
	 * Removes the specified members from the sorted set stored at key. Non existing
	 * members are ignored.
	 * 
	 * An error is returned when key exists and does not hold a sorted set.
	 * 
	 * @param key
	 * @param members
	 * @return Integer reply, specifically:
	 * 
	 *         The number of members removed from the sorted set, not including non
	 *         existing members.
	 */
	RedisResponse<Long> zrem(K key, V... members);

	/**
	 * https://redis.io/commands/zremrangebylex<br/>
	 * <br/>
	 * When all the elements in a sorted set are inserted with the same score, in
	 * order to force lexicographical ordering, this command removes all elements in
	 * the sorted set stored at key between the lexicographical range specified by
	 * min and max.
	 * 
	 * The meaning of min and max are the same of the ZRANGEBYLEX command.
	 * Similarly, this command actually returns the same elements that ZRANGEBYLEX
	 * would return if called with the same min and max arguments.
	 * 
	 * @param key
	 * @param min
	 * @param max
	 * @return Integer reply: the number of elements removed.
	 */
	RedisResponse<Long> zremrangebylex(K key, Range<V> range);

	/**
	 * https://redis.io/commands/zremrangebyrank<br/>
	 * <br/>
	 * 
	 * Removes all elements in the sorted set stored at key with rank between start
	 * and stop. Both start and stop are 0 -based indexes with 0 being the element
	 * with the lowest score. These indexes can be negative numbers, where they
	 * indicate offsets starting at the element with the highest score. For example:
	 * -1 is the element with the highest score, -2 the element with the second
	 * highest score and so forth.
	 * 
	 * @param key
	 * @param start
	 * @param stop
	 * @return Integer reply: the number of elements removed.
	 */
	RedisResponse<Long> zremrangebyrank(K key, long start, long stop);

	/**
	 * https://redis.io/commands/zremrangebyscore<br/>
	 * <br/>
	 * Removes all elements in the sorted set stored at key with a score between min
	 * and max (inclusive).
	 * 
	 * Since version 2.1.6, min and max can be exclusive, following the syntax of
	 * ZRANGEBYSCORE.
	 * 
	 * @param key
	 * @param max
	 * @param limit
	 * @return Integer reply: the number of elements removed.
	 */
	RedisResponse<Long> zremrangebyscore(K key, Range<V> range);

	/**
	 * https://redis.io/commands/zrevrange<br/>
	 * <br/>
	 * Returns the specified range of elements in the sorted set stored at key. The
	 * elements are considered to be ordered from the highest to the lowest score.
	 * Descending lexicographical order is used for elements with equal score.
	 * 
	 * Apart from the reversed ordering, ZREVRANGE is similar to ZRANGE.
	 * 
	 * As per Redis 6.2.0, this command is considered deprecated. Please prefer
	 * using the ZRANGE command with the REV argument in new code.
	 * 
	 * @return Array reply: list of elements in the specified range (optionally with
	 *         their scores).
	 */
	RedisResponse<Collection<V>> zrevrange(K key, long start, long stop);

	/**
	 * https://redis.io/commands/zrevrangebylex<br/>
	 * <br/>
	 * 
	 * When all the elements in a sorted set are inserted with the same score, in
	 * order to force lexicographical ordering, this command returns all the
	 * elements in the sorted set at key with a value between max and min.
	 * 
	 * Apart from the reversed ordering, ZREVRANGEBYLEX is similar to ZRANGEBYLEX.
	 * 
	 * As per Redis 6.2.0, this command is considered deprecated. Please prefer
	 * using the ZRANGE command with the BYLEX and REV arguments in new code.
	 * 
	 * @return Array reply: list of elements in the specified score range.
	 */
	RedisResponse<Collection<V>> zrevrangebylex(K key, Range<V> range, int offset, int count);

	/**
	 * https://redis.io/commands/zrevrangebyscore<br/>
	 * <br/>
	 * Returns all the elements in the sorted set at key with a score between max
	 * and min (including elements with score equal to max or min). In contrary to
	 * the default ordering of sorted sets, for this command the elements are
	 * considered to be ordered from high to low scores.
	 * 
	 * The elements having the same score are returned in reverse lexicographical
	 * order.
	 * 
	 * Apart from the reversed ordering, ZREVRANGEBYSCORE is similar to
	 * ZRANGEBYSCORE.
	 * 
	 * As per Redis 6.2.0, this command is considered deprecated. Please prefer
	 * using the ZRANGE command with the BYSCORE and REV arguments in new code.
	 * 
	 * @param key
	 * @param max
	 * @param min
	 * @param option
	 * @param limit
	 * @return Array reply: list of elements in the specified score range
	 *         (optionally with their scores).
	 */
	RedisResponse<Collection<V>> zrevrangebyscore(K key, Range<V> range, int offset, int count);

	RedisResponse<Collection<Tuple<V>>> zrevrangebyscoreWithScores(K key, Range<V> range, int offset, int count);

	/**
	 * https://redis.io/commands/zrevrank<br/>
	 * <br/>
	 * Returns the rank of member in the sorted set stored at key, with the scores
	 * ordered from high to low. The rank (or index) is 0-based, which means that
	 * the member with the highest score has rank 0.
	 * 
	 * Use ZRANK to get the rank of an element with the scores ordered from low to
	 * high.
	 * 
	 * @param key
	 * @param member
	 * @return Return value If member exists in the sorted set, Integer reply: the
	 *         rank of member. If member does not exist in the sorted set or key
	 *         does not exist, Bulk string reply: nil.
	 */
	RedisResponse<Long> zrevrank(K key, V member);

	/**
	 * https://redis.io/commands/zscore<br/>
	 * <br/>
	 * Returns the score of member in the sorted set at key.
	 * 
	 * If member does not exist in the sorted set, or key does not exist, nil is
	 * returned.
	 * 
	 * @param key
	 * @param member
	 * @return Bulk string reply: the score of member (a double precision floating
	 *         point number), represented as string.
	 */
	RedisResponse<Double> zscore(K key, V member);

	/**
	 * https://redis.io/commands/zunion<br/>
	 * <br/>
	 * This command is similar to ZUNIONSTORE, but instead of storing the resulting
	 * sorted set, it is returned to the client.
	 * 
	 * For a description of the WEIGHTS and AGGREGATE options, see ZUNIONSTORE.
	 * 
	 * @param keys
	 * @param weights
	 * @param option
	 * @param withOption
	 * @return Array reply: the result of union (optionally with their scores, in
	 *         case the WITHSCORES option is given).
	 */
	RedisResponse<Collection<V>> zunion(InterArgs interArgs, K... keys);

	RedisResponse<Collection<Tuple<V>>> zunionWithScores(InterArgs interArgs, K... keys);

	/**
	 * https://redis.io/commands/zunionstore<br/>
	 * <br/>
	 * Computes the union of numkeys sorted sets given by the specified keys, and
	 * stores the result in destination. It is mandatory to provide the number of
	 * input keys (numkeys) before passing the input keys and the other (optional)
	 * arguments.
	 * 
	 * By default, the resulting score of an element is the sum of its scores in the
	 * sorted sets where it exists.
	 * 
	 * Using the WEIGHTS option, it is possible to specify a multiplication factor
	 * for each input sorted set. This means that the score of every element in
	 * every input sorted set is multiplied by this factor before being passed to
	 * the aggregation function. When WEIGHTS is not given, the multiplication
	 * factors default to 1.
	 * 
	 * With the AGGREGATE option, it is possible to specify how the results of the
	 * union are aggregated. This option defaults to SUM, where the score of an
	 * element is summed across the inputs where it exists. When this option is set
	 * to either MIN or MAX, the resulting set will contain the minimum or maximum
	 * score of an element across the inputs where it exists.
	 * 
	 * If destination already exists, it is overwritten.
	 * 
	 * @return Integer reply: the number of elements in the resulting sorted set at
	 *         destination.
	 */
	RedisResponse<Long> zunionstore(K destinationKey, InterArgs interArgs, K... keys);
}
