package io.basc.framework.redis.async;

public interface AsyncRedisConnectionCommands<K, V> {
	/**
	 * https://redis.io/commands/ping<br/>
	 * <br/>
	 * Returns PONG if no argument is provided, otherwise return a copy of the
	 * argument as a bulk. This command is often used to test if a connection is
	 * still alive, or to measure latency.
	 * 
	 * If the client is subscribed to a channel or a pattern, it will instead return
	 * a multi-bulk with a "pong" in the first position and an empty bulk in the
	 * second position, unless an argument is provided in which case it returns a
	 * copy of the argument.
	 * 
	 * @param message
	 * @return Simple string reply
	 */
	Response<V> ping(K message);

	/**
	 * https://redis.io/commands/select<br/>
	 * <br/>
	 * Select the Redis logical database having the specified zero-based numeric
	 * index. New connections always use the database 0.
	 * 
	 * Selectable Redis databases are a form of namespacing: all databases are still
	 * persisted in the same RDB / AOF file. However different databases can have
	 * keys with the same name, and commands like FLUSHDB, SWAPDB or RANDOMKEY work
	 * on specific databases.
	 * 
	 * In practical terms, Redis databases should be used to separate different keys
	 * belonging to the same application (if needed), and not to use a single Redis
	 * instance for multiple unrelated applications.
	 * 
	 * When using Redis Cluster, the SELECT command cannot be used, since Redis
	 * Cluster only supports database zero. In the case of a Redis Cluster, having
	 * multiple databases would be useless and an unnecessary source of complexity.
	 * Commands operating atomically on a single database would not be possible with
	 * the Redis Cluster design and goals.
	 * 
	 * Since the currently selected database is a property of the connection,
	 * clients should track the currently selected database and re-select it on
	 * reconnection. While there is no command in order to query the selected
	 * database in the current connection, the CLIENT LIST output shows, for each
	 * client, the currently selected database.
	 * 
	 * @param index
	 * @return
	 */
	Response<String> select(int index);
}
