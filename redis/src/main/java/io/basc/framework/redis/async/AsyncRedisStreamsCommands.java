package io.basc.framework.redis.async;

import java.util.List;

import io.basc.framework.redis.ClaimArgs;

@SuppressWarnings("unchecked")
public interface AsyncRedisStreamsCommands<K, V> {
	/**
	 * https://redis.io/commands/xack<br/>
	 * <br/>
	 * 
	 * The XACK command removes one or multiple messages from the Pending Entries
	 * List (PEL) of a stream consumer group. A message is pending, and as such
	 * stored inside the PEL, when it was delivered to some consumer, normally as a
	 * side effect of calling XREADGROUP, or when a consumer took ownership of a
	 * message calling XCLAIM. The pending message was delivered to some consumer
	 * but the server is yet not sure it was processed at least once. So new calls
	 * to XREADGROUP to grab the messages history for a consumer (for instance using
	 * an ID of 0), will return such message. Similarly the pending message will be
	 * listed by the XPENDING command, that inspects the PEL.
	 * 
	 * Once a consumer successfully processes a message, it should call XACK so that
	 * such message does not get processed again, and as a side effect, the PEL
	 * entry about this message is also purged, releasing memory from the Redis
	 * server.
	 * 
	 * @param key
	 * @param group
	 * @param ids
	 * @return Integer reply, specifically:
	 * 
	 *         The command returns the number of messages successfully acknowledged.
	 *         Certain message IDs may no longer be part of the PEL (for example
	 *         because they have already been acknowledged), and XACK will not count
	 *         them as successfully acknowledged.
	 */
	Response<Long> xack(K key, K group, K... ids);

	/**
	 * https://redis.io/commands/xclaim
	 * 
	 * @return Array reply, specifically:
	 * 
	 *         The command returns all the messages successfully claimed, in the
	 *         same format as XRANGE. However if the JUSTID option was specified,
	 *         only the message IDs are reported, without including the actual
	 *         message.
	 */
	Response<List<V>> xclaim(K key, K group, K consumer, long minIdleTime, ClaimArgs args, K... ids);

	/**
	 * https://redis.io/commands/xdel
	 * 
	 * @param key
	 * @param ids
	 * @return Integer reply: the number of entries actually deleted.
	 */
	Response<Long> xdel(K key, K... ids);
}
