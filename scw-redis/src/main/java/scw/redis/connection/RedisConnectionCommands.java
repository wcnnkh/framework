package scw.redis.connection;

public interface RedisConnectionCommands {
	/**
	 * https://redis.io/commands/auth
	 * 
	 * @param username
	 * @param password
	 * @return Simple string reply or an error if the password, or
	 *         username/password pair, is invalid.
	 */
	byte[] auth(byte[] username, byte[] password);

	/**
	 * https://redis.io/commands/client-caching<br/>
	 * <br/>
	 * This command controls the tracking of the keys in the next command
	 * executed by the connection, when tracking is enabled in OPTIN or OPTOUT
	 * mode. Please check the client side caching documentation for background
	 * information.
	 * 
	 * When tracking is enabled Redis, using the CLIENT TRACKING command, it is
	 * possible to specify the OPTIN or OPTOUT options, so that keys in read
	 * only commands are not automatically remembered by the server to be
	 * invalidated later. When we are in OPTIN mode, we can enable the tracking
	 * of the keys in the next command by calling CLIENT CACHING yes immediately
	 * before it. Similarly when we are in OPTOUT mode, and keys are normally
	 * tracked, we can avoid the keys in the next command to be tracked using
	 * CLIENT CACHING no.
	 * 
	 * Basically the command sets a state in the connection, that is valid only
	 * for the next command execution, that will modify the behavior of client
	 * tracking.
	 * 
	 * @param yes
	 * @return Simple string reply: OK or an error if the argument is not yes or
	 *         no.
	 */
	byte[] client_caching(boolean yes);

	/**
	 * https://redis.io/commands/client-getname<br/>
	 * <br/>
	 * The CLIENT GETNAME returns the name of the current connection as set by
	 * CLIENT SETNAME. Since every new connection starts without an associated
	 * name, if no name was assigned a null bulk reply is returned.
	 * 
	 * @return Bulk string reply: The connection name, or a null bulk reply if
	 *         no name is set.
	 */
	byte[] client_getmame();

	/**
	 * https://redis.io/commands/client-getredir<br/>
	 * <br/>
	 * This command returns the client ID we are redirecting our tracking
	 * notifications to. We set a client to redirect to when using CLIENT
	 * TRACKING to enable tracking. However in order to avoid forcing client
	 * libraries implementations to remember the ID notifications are redirected
	 * to, this command exists in order to improve introspection and allow
	 * clients to check later if redirection is active and towards which client
	 * ID.
	 * 
	 * @return Integer reply: the ID of the client we are redirecting the
	 *         notifications to. The command returns -1 if client tracking is
	 *         not enabled, or 0 if client tracking is enabled but we are not
	 *         redirecting the notifications to any client.
	 */
	Integer client_getredir();

	/**
	 * https://redis.io/commands/client-id<br/>
	 * <br/>
	 * The command just returns the ID of the current connection. Every
	 * connection ID has certain guarantees:
	 * 
	 * It is never repeated, so if CLIENT ID returns the same number, the caller
	 * can be sure that the underlying client did not disconnect and reconnect
	 * the connection, but it is still the same connection. The ID is
	 * monotonically incremental. If the ID of a connection is greater than the
	 * ID of another connection, it is guaranteed that the second connection was
	 * established with the server at a later time. This command is especially
	 * useful together with CLIENT UNBLOCK which was introduced also in Redis 5
	 * together with CLIENT ID. Check the CLIENT UNBLOCK command page for a
	 * pattern involving the two commands.
	 * 
	 * @return Integer reply
	 */
	Integer client_id();

	/**
	 * https://redis.io/commands/client-info<br/>
	 * <br/>
	 * The command returns information and statistics about the current client
	 * connection in a mostly human readable format.
	 * 
	 * The reply format is identical to that of CLIENT LIST, and the content
	 * consists only of information about the current client.
	 * 
	 * @return Bulk string reply: a unique string, as described at the CLIENT
	 *         LIST page, for the current client.
	 */
	byte[] client_info();

	/**
	 * https://redis.io/commands/ping<br/>
	 * <br/>
	 * Returns PONG if no argument is provided, otherwise return a copy of the
	 * argument as a bulk. This command is often used to test if a connection is
	 * still alive, or to measure latency.
	 * 
	 * If the client is subscribed to a channel or a pattern, it will instead
	 * return a multi-bulk with a "pong" in the first position and an empty bulk
	 * in the second position, unless an argument is provided in which case it
	 * returns a copy of the argument.
	 * 
	 * @param message
	 * @return Simple string reply
	 */
	byte[] ping(byte[] message);

	/**
	 * https://redis.io/commands/quit<br/>
	 * <br/>
	 * Ask the server to close the connection. The connection is closed as soon
	 * as all pending replies have been written to the client.
	 * 
	 * @return Simple string reply: always OK.
	 */
	byte[] quit();

	/**
	 * https://redis.io/commands/reset<br/>
	 * <br/>
	 * This command performs a full reset of the connection's server-side
	 * context, mimicking the effect of disconnecting and reconnecting again.
	 * 
	 * When the command is called from a regular client connection, it does the
	 * following:
	 * 
	 * Discards the current MULTI transaction block, if one exists. Unwatches
	 * all keys WATCHed by the connection. Disables CLIENT TRACKING, if in use.
	 * Sets the connection to READWRITE mode. Cancels the connection's ASKING
	 * mode, if previously set. Sets CLIENT REPLY to ON. Sets the protocol
	 * version to RESP2. SELECTs database 0. Exits MONITOR mode, when
	 * applicable. Aborts Pub/Sub's subscription state (SUBSCRIBE and
	 * PSUBSCRIBE), when appropriate. Deauthenticates the connection, requiring
	 * a call AUTH to reauthenticate when authentication is enabled.
	 * 
	 * @return Simple string reply: always 'RESET'.
	 */
	byte[] reset();

	/**
	 * https://redis.io/commands/select<br/>
	 * <br/>
	 * Select the Redis logical database having the specified zero-based numeric
	 * index. New connections always use the database 0.
	 * 
	 * Selectable Redis databases are a form of namespacing: all databases are
	 * still persisted in the same RDB / AOF file. However different databases
	 * can have keys with the same name, and commands like FLUSHDB, SWAPDB or
	 * RANDOMKEY work on specific databases.
	 * 
	 * In practical terms, Redis databases should be used to separate different
	 * keys belonging to the same application (if needed), and not to use a
	 * single Redis instance for multiple unrelated applications.
	 * 
	 * When using Redis Cluster, the SELECT command cannot be used, since Redis
	 * Cluster only supports database zero. In the case of a Redis Cluster,
	 * having multiple databases would be useless and an unnecessary source of
	 * complexity. Commands operating atomically on a single database would not
	 * be possible with the Redis Cluster design and goals.
	 * 
	 * Since the currently selected database is a property of the connection,
	 * clients should track the currently selected database and re-select it on
	 * reconnection. While there is no command in order to query the selected
	 * database in the current connection, the CLIENT LIST output shows, for
	 * each client, the currently selected database.
	 * 
	 * @param index
	 * @return
	 */
	byte[] select(int index);
}
