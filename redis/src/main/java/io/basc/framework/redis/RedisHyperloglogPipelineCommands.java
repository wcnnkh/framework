package io.basc.framework.redis;

@SuppressWarnings("unchecked")
public interface RedisHyperloglogPipelineCommands<K, V> {
	/**
	 * https://redis.io/commands/pfadd<br/>
	 * <br/>
	 * Adds all the element arguments to the HyperLogLog data structure stored at
	 * the variable name specified as first argument.
	 * 
	 * As a side effect of this command the HyperLogLog internals may be updated to
	 * reflect a different estimation of the number of unique items added so far
	 * (the cardinality of the set).
	 * 
	 * If the approximated cardinality estimated by the HyperLogLog changed after
	 * executing the command, PFADD returns 1, otherwise 0 is returned. The command
	 * automatically creates an empty HyperLogLog structure (that is, a Redis String
	 * of a specified length and with a given encoding) if the specified key does
	 * not exist.
	 * 
	 * To call the command without elements but just the variable name is valid,
	 * this will result into no operation performed if the variable already exists,
	 * or just the creation of the data structure if the key does not exist (in the
	 * latter case 1 is returned).
	 * 
	 * For an introduction to HyperLogLog data structure check the PFCOUNT command
	 * page.
	 * 
	 * @param key
	 * @param elements
	 * @return Integer reply, specifically:
	 * 
	 *         1 if at least 1 HyperLogLog internal register was altered. 0
	 *         otherwise.
	 */
	RedisResponse<Long> pfadd(K key, V... elements);

	/**
	 * https://redis.io/commands/pfcount<br/>
	 * <br/>
	 * When called with a single key, returns the approximated cardinality computed
	 * by the HyperLogLog data structure stored at the specified variable, which is
	 * 0 if the variable does not exist.
	 * 
	 * When called with multiple keys, returns the approximated cardinality of the
	 * union of the HyperLogLogs passed, by internally merging the HyperLogLogs
	 * stored at the provided keys into a temporary HyperLogLog.
	 * 
	 * The HyperLogLog data structure can be used in order to count unique elements
	 * in a set using just a small constant amount of memory, specifically 12k bytes
	 * for every HyperLogLog (plus a few bytes for the key itself).
	 * 
	 * The returned cardinality of the observed set is not exact, but approximated
	 * with a standard error of 0.81%.
	 * 
	 * For example in order to take the count of all the unique search queries
	 * performed in a day, a program needs to call PFADD every time a query is
	 * processed. The estimated number of unique queries can be retrieved with
	 * PFCOUNT at any time.
	 * 
	 * Note: as a side effect of calling this function, it is possible that the
	 * HyperLogLog is modified, since the last 8 bytes encode the latest computed
	 * cardinality for caching purposes. So PFCOUNT is technically a write command.
	 * 
	 * @return Integer reply, specifically:
	 * 
	 *         The approximated number of unique elements observed via PFADD.
	 */
	RedisResponse<Long> pfcount(K... keys);

	/**
	 * https://redis.io/commands/pfmerge<br/>
	 * <br/>
	 * Merge multiple HyperLogLog values into an unique value that will approximate
	 * the cardinality of the union of the observed Sets of the source HyperLogLog
	 * structures.
	 * 
	 * The computed merged HyperLogLog is set to the destination variable, which is
	 * created if does not exist (defaulting to an empty HyperLogLog).
	 * 
	 * If the destination variable exists, it is treated as one of the source sets
	 * and its cardinality will be included in the cardinality of the computed
	 * HyperLogLog.
	 * 
	 * @param destKey
	 * @param sourceKeys
	 * @return Simple string reply: The command just returns OK.
	 */
	RedisResponse<String> pfmerge(K destKey, K... sourceKeys);
}
