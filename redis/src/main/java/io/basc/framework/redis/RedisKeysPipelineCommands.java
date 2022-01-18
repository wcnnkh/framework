package io.basc.framework.redis;

import java.util.Set;

/**
 * https://redis.io/commands#generic
 * 
 * @author shuchaowen
 *
 */
@SuppressWarnings("unchecked")
public interface RedisKeysPipelineCommands<K, V> {
	/**
	 * https://redis.io/commands/copy
	 * 
	 * @param source
	 * @param destination
	 * @return 1 if source was copied. 0 if source was not copied.
	 */
	RedisResponse<Boolean> copy(K source, K destination, boolean replace);

	/**
	 * https://redis.io/commands/del
	 * 
	 * @param keys
	 * @return The number of keys that were removed.
	 */
	RedisResponse<Long> del(K... keys);

	/**
	 * https://redis.io/commands/dump
	 * 
	 * @param key
	 * @return Bulk string reply: the serialized value.
	 */
	RedisResponse<V> dump(K key);

	/**
	 * https://redis.io/commands/exists
	 * 
	 * @param keys
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the key exists. 0 if the key does not exist. <br/>
	 *         Since Redis 3.0.3 the command accepts a variable number of keys and
	 *         the return value is generalized:
	 * 
	 *         The number of keys existing among the ones specified as arguments.
	 *         Keys mentioned multiple times and existing are counted multiple
	 *         times.
	 */
	RedisResponse<Long> exists(K... keys);

	/**
	 * https://redis.io/commands/expire
	 * 
	 * @param key
	 * @param seconds
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the timeout was set. 0 if key does not exist.
	 */
	RedisResponse<Long> expire(K key, long seconds);

	/**
	 * https://redis.io/commands/expireat
	 * 
	 * @param key
	 * @param timestamp
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the timeout was set. 0 if key does not exist.
	 */
	RedisResponse<Long> expireAt(K key, long timestamp);

	/**
	 * Supported glob-style patterns:
	 * 
	 * h?llo matches hello, hallo and hxllo h*llo matches hllo and heeeello h[ae]llo
	 * matches hello and hallo, but not hillo h[^e]llo matches hallo, hbllo, ... but
	 * not hello h[a-b]llo matches hallo and hbllo Use \ to escape special
	 * characters if you want to match them verbatim. <br/>
	 * 
	 * https://redis.io/commands/keys
	 * 
	 * @param pattern
	 * @return Array reply: list of keys matching pattern.
	 */
	RedisResponse<Set<K>> keys(K pattern);

	RedisResponse<String> migrate(String host, int port, K key, int targetDB, int timeout);

	/**
	 * https://redis.io/commands/migrate
	 * 
	 * @param host
	 * @param port
	 * @param key
	 * @param targetDB
	 * @param timeout
	 * @param option   COPY -- Do not remove the key from the local instance.<br/>
	 *                 REPLACE -- Replace existing key on the remote instance. <br/>
	 *                 KEYS -- If the key argument is an empty string, the command
	 *                 will instead migrate all the keys that follow the KEYS option
	 *                 (see the above section for more info). <br/>
	 *                 AUTH -- Authenticate with the given password to the remote
	 *                 instance.<br/>
	 *                 AUTH2 -- Authenticate with the given username and password
	 *                 pair (Redis 6 or greater ACL auth style).
	 * @param keys
	 * @return Simple string reply: The command returns OK on success, or NOKEY if
	 *         no keys were found in the source instance.
	 */
	RedisResponse<String> migrate(String host, int port, int targetDB, int timeout, boolean copy, boolean replace, RedisAuth auth,
			K... keys);

	/**
	 * https://redis.io/commands/move
	 * 
	 * @param key
	 * @param targetDB
	 * @return Integer reply, specifically:
	 * 
	 *         1 if key was moved. 0 if key was not moved.
	 */
	RedisResponse<Long> move(K key, int targetDB);

	/**
	 * https://redis.io/commands/object
	 * 
	 * @param key
	 * @return
	 */
	RedisResponse<Long> objectRefCount(K key);

	/**
	 * https://redis.io/commands/object
	 * 
	 * @param key
	 * @return
	 */
	RedisResponse<RedisValueEncoding> objectEncoding(K key);

	/**
	 * https://redis.io/commands/object
	 * 
	 * @param key
	 * @return
	 */
	RedisResponse<Long> objectIdletime(K key);

	RedisResponse<Long> objectFreq(K key);

	/**
	 * https://redis.io/commands/persist
	 * 
	 * @param key
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the timeout was removed. 0 if key does not exist or does not
	 *         have an associated timeout.
	 */
	RedisResponse<Long> persist(K key);

	/**
	 * https://redis.io/commands/pexpire
	 * 
	 * @param key
	 * @param milliseconds
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the timeout was set. 0 if key does not exist.
	 */
	RedisResponse<Long> pexpire(K key, long milliseconds);

	/**
	 * PEXPIREAT has the same effect and semantic as EXPIREAT, but the Unix time at
	 * which the key will expire is specified in milliseconds instead of
	 * seconds.<br/>
	 * 
	 * https://redis.io/commands/pexpireat
	 * 
	 * @param key
	 * @param timestamp
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the timeout was set. 0 if key does not exist.
	 */
	RedisResponse<Long> pexpireAt(K key, long timestamp);

	/**
	 * Like TTL this command returns the remaining time to live of a key that has an
	 * expire set, with the sole difference that TTL returns the amount of remaining
	 * time in seconds while PTTL returns it in milliseconds.
	 * 
	 * In Redis 2.6 or older the command returns -1 if the key does not exist or if
	 * the key exist but has no associated expire.
	 * 
	 * Starting with Redis 2.8 the return value in case of error changed:
	 * 
	 * The command returns -2 if the key does not exist. The command returns -1 if
	 * the key exists but has no associated expire. <br/>
	 *
	 * https://redis.io/commands/pttl
	 * 
	 * @param key
	 * @return Integer reply: TTL in milliseconds, or a negative value in order to
	 *         signal an error (see the description above).
	 */
	RedisResponse<Long> pttl(K key);

	/**
	 * Return a random key from the currently selected database. <br/>
	 * https://redis.io/commands/randomkey
	 * 
	 * @return Bulk string reply: the random key, or nil when the database is empty.
	 */
	RedisResponse<K> randomkey();

	/**
	 * Renames key to newkey. It returns an error when key does not exist. If newkey
	 * already exists it is overwritten, when this happens RENAME executes an
	 * implicit DEL operation, so if the deleted key contains a very big value it
	 * may cause high latency even if RENAME itself is usually a constant-time
	 * operation.
	 * 
	 * In Cluster mode, both key and newkey must be in the same hash slot, meaning
	 * that in practice only keys that have the same hash tag can be reliably
	 * renamed in cluster. <br/>
	 * 
	 * <br/>
	 * History <= 3.2.0: Before Redis 3.2.0, an error is returned if source and
	 * destination names are the same. <br/>
	 * <br/>
	 * https://redis.io/commands/rename
	 * 
	 * @param key
	 * @param newKey
	 * @return Return value Simple string reply
	 */
	RedisResponse<String> rename(K key, K newKey);

	/**
	 * Renames key to newkey if newkey does not yet exist. It returns an error when
	 * key does not exist.
	 * 
	 * In Cluster mode, both key and newkey must be in the same hash slot, meaning
	 * that in practice only keys that have the same hash tag can be reliably
	 * renamed in cluster. <br/>
	 * <br/>
	 * History <= 3.2.0: Before Redis 3.2.0, an error is returned if source and
	 * destination names are the same. <br/>
	 * <br/>
	 * https://redis.io/commands/renamenx
	 * 
	 * @param key
	 * @param newKey
	 * @return Integer reply, specifically:
	 * 
	 *         1 if key was renamed to newkey. 0 if newkey already exists.
	 */
	RedisResponse<Boolean> renamenx(K key, K newKey);

	/**
	 * Create a key associated with a value that is obtained by deserializing the
	 * provided serialized value (obtained via DUMP).
	 * 
	 * If ttl is 0 the key is created without any expire, otherwise the specified
	 * expire time (in milliseconds) is set.
	 * 
	 * If the ABSTTL modifier was used, ttl should represent an absolute Unix
	 * timestamp (in milliseconds) in which the key will expire. (Redis 5.0 or
	 * greater).
	 * 
	 * For eviction purposes, you may use the IDLETIME or FREQ modifiers. See OBJECT
	 * for more information (Redis 5.0 or greater).
	 * 
	 * RESTORE will return a "Target key name is busy" error when key already exists
	 * unless you use the REPLACE modifier (Redis 3.0 or greater).
	 * 
	 * RESTORE checks the RDB version and data checksum. If they don't match an
	 * error is returned. <br/>
	 * <br/>
	 * https://redis.io/commands/restore
	 * 
	 * @param key
	 * @param ttl
	 * @param serializedValue
	 * @return Simple string reply: The command returns OK on success.
	 */
	RedisResponse<String> restore(K key, long ttl, byte[] serializedValue, boolean replace, boolean absTtl, Long idleTime,
			Long frequency);

	/**
	 * https://redis.io/commands/scan
	 * 
	 * @param cursor
	 * @param pattern
	 * @param count   While SCAN does not provide guarantees about the number of
	 *                elements returned at every iteration, it is possible to
	 *                empirically adjust the behavior of SCAN using the COUNT
	 *                option. Basically with COUNT the user specified the amount of
	 *                work that should be done at every call in order to retrieve
	 *                elements from the collection. This is just a hint for the
	 *                implementation, however generally speaking this is what you
	 *                could expect most of the times from the implementation.
	 * 
	 *                The default COUNT value is 10. When iterating the key space,
	 *                or a Set, Hash or Sorted Set that is big enough to be
	 *                represented by a hash table, assuming no MATCH option is used,
	 *                the server will usually return count or a bit more than count
	 *                elements per call. Please check the why SCAN may return all
	 *                the elements at once section later in this document. When
	 *                iterating Sets encoded as intsets (small sets composed of just
	 *                integers), or Hashes and Sorted Sets encoded as ziplists
	 *                (small hashes and sets composed of small individual values),
	 *                usually all the elements are returned in the first SCAN call
	 *                regardless of the COUNT value.
	 * 
	 *                Important: there is no need to use the same COUNT value for
	 *                every iteration. The caller is free to change the count from
	 *                one iteration to the other as required, as long as the cursor
	 *                passed in the next call is the one obtained in the previous
	 *                call to the command.
	 * @return
	 */
	RedisResponse<Cursor<K>> scan(long cursorId, ScanOptions<K> options);

	/**
	 * https://redis.io/commands/touch
	 * 
	 * @param keys Alters the last access time of a key(s). A key is ignored if it
	 *             does not exist.
	 * @return Integer reply: The number of keys that were touched.
	 */
	RedisResponse<Long> touch(K... keys);

	/**
	 * https://redis.io/commands/ttl <br/>
	 * <br/>
	 * Returns the remaining time to live of a key that has a timeout. This
	 * introspection capability allows a Redis client to check how many seconds a
	 * given key will continue to be part of the dataset.
	 * 
	 * In Redis 2.6 or older the command returns -1 if the key does not exist or if
	 * the key exist but has no associated expire.
	 * 
	 * Starting with Redis 2.8 the return value in case of error changed:
	 * 
	 * The command returns -2 if the key does not exist. The command returns -1 if
	 * the key exists but has no associated expire. See also the PTTL command that
	 * returns the same information with milliseconds resolution (Only available in
	 * Redis 2.6 or greater).
	 * 
	 * @param key
	 * @return Integer reply: TTL in seconds, or a negative value in order to signal
	 *         an error (see the description above).
	 */
	RedisResponse<Long> ttl(K key);

	/**
	 * https://redis.io/commands/type<br/>
	 * <br/>
	 * Returns the string representation of the type of the value stored at key. The
	 * different types that can be returned are: string, list, set, zset, hash and
	 * stream.
	 * 
	 * @param key
	 * @return Simple string reply: type of key, or none when key does not exist.
	 */
	RedisResponse<DataType> type(K key);

	/**
	 * https://redis.io/commands/unlink<br/>
	 * <br/>
	 * This command is very similar to DEL: it removes the specified keys. Just like
	 * DEL a key is ignored if it does not exist. However the command performs the
	 * actual memory reclaiming in a different thread, so it is not blocking, while
	 * DEL is. This is where the command name comes from: the command just unlinks
	 * the keys from the keyspace. The actual removal will happen later
	 * asynchronously.
	 * 
	 * @param keys
	 * @return Integer reply: The number of keys that were unlinked.
	 */
	RedisResponse<Long> unlink(K... keys);

	/**
	 * https://redis.io/commands/wait<br/>
	 * 
	 * @param numreplicas
	 * @param timeout
	 * @return Integer reply: The command returns the number of replicas reached by
	 *         all the writes performed in the context of the current connection.
	 */
	RedisResponse<Long> wait(int numreplicas, long timeout);
}
