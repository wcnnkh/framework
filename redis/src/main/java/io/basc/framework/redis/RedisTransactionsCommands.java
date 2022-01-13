package io.basc.framework.redis;

import java.util.List;

@SuppressWarnings("unchecked")
public interface RedisTransactionsCommands<K> {
	/**
	 * https://redis.io/commands/discard<br/>
	 * <br/>
	 * Flushes all previously queued commands in a transaction and restores the
	 * connection state to normal.
	 * 
	 * If WATCH was used, DISCARD unwatches all keys watched by the connection.
	 * 
	 * @return Simple string reply: always OK.
	 */
	void discard();

	/**
	 * https://redis.io/commands/exec<br/>
	 * <br/>
	 * Executes all previously queued commands in a transaction and restores the
	 * connection state to normal.
	 * 
	 * When using WATCH, EXEC will execute commands only if the watched keys were
	 * not modified, allowing for a check-and-set mechanism.
	 * 
	 * @return Array reply: each element being the reply to each of the commands in
	 *         the atomic transaction.
	 * 
	 *         When using WATCH, EXEC can return a Null reply if the execution was
	 *         aborted.
	 */
	List<Object> exec();

	/**
	 * https://redis.io/commands/multi<br/>
	 * <br/>
	 * Marks the start of a transaction block. Subsequent commands will be queued
	 * for atomic execution using EXEC.
	 * 
	 * @return Simple string reply: always OK.
	 */
	void multi();

	/**
	 * https://redis.io/commands/unwatch<br/>
	 * <br/>
	 * Flushes all the previously watched keys for a transaction.
	 * 
	 * If you call EXEC or DISCARD, there's no need to manually call UNWATCH.
	 * 
	 * @return Simple string reply: always OK.
	 */
	void unwatch();

	/**
	 * https://redis.io/commands/watch<br/>
	 * <br/>
	 * 
	 * Marks the given keys to be watched for conditional execution of a
	 * transaction.
	 * 
	 * @param keys
	 * @return Simple string reply: always OK.
	 */
	void watch(K... keys);
}
