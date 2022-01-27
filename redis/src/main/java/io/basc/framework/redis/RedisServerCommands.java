package io.basc.framework.redis;

import java.util.List;

import io.basc.framework.lang.Nullable;

@SuppressWarnings("unchecked")
public interface RedisServerCommands<K, V> {
	default List<K> aclCat() {
		return aclCat(null);
	}

	/**
	 * https://redis.io/commands/acl-cat
	 * 
	 * @param categoryname
	 * @return Array reply: a list of ACL categories or a list of commands inside a
	 *         given category. The command may return an error if an invalid
	 *         category name is given as argument.
	 */
	List<K> aclCat(@Nullable K categoryname);

	Long aclDelUser(K username, K... usernames);

	default String aclGenPass() {
		return aclGenPass(null);
	}

	String aclGenPass(@Nullable Integer bits);

	/**
	 * https://redis.io/commands/acl-list
	 * 
	 * @return An array of strings.
	 */
	List<K> aclList();

	/**
	 * https://redis.io/commands/acl-load
	 * 
	 * @return Simple string reply: OK on success.
	 * 
	 *         The command may fail with an error for several reasons: if the file
	 *         is not readable, if there is an error inside the file, and in such
	 *         case the error will be reported to the user in the error. Finally the
	 *         command will fail if the server is not configured to use an external
	 *         ACL file.
	 */
	String aclLoad();

	/**
	 * https://redis.io/commands/acl-log
	 * 
	 * @return a list of ACL security events.
	 */
	default List<K> aclLog() {
		return aclLog(null);
	}

	/**
	 * https://redis.io/commands/acl-log
	 * 
	 * @param count
	 * @return a list of ACL security events.
	 */
	List<K> aclLog(@Nullable Integer count);

	/**
	 * https://redis.io/commands/acl-log
	 * 
	 * @param count
	 * @return OK if the security log was cleared.
	 */
	String aclLogReset();

	/**
	 * https://redis.io/commands/acl-save
	 * 
	 * @return Simple string reply: OK on success.
	 * 
	 *         The command may fail with an error for several reasons: if the file
	 *         cannot be written or if the server is not configured to use an
	 *         external ACL file.
	 */
	String aclSave();

	/**
	 * https://redis.io/commands/acl-setuser
	 * 
	 * @param username
	 * @param rules
	 * @return Simple string reply: OK on success.
	 * 
	 *         If the rules contain errors, the error is returned.
	 */
	String aclSetuser(K username, K... rules);

	/**
	 * https://redis.io/commands/acl-users
	 * 
	 * @return An array of strings.
	 */
	List<K> aclUsers();

	/**
	 * https://redis.io/commands/acl-whoami
	 * 
	 * @return Bulk string reply: the username of the current connection.
	 */
	K aclWhoami();

	/**
	 * https://redis.io/commands/bgrewriteaof
	 * 
	 * @return Simple string reply: A simple string reply indicating that the
	 *         rewriting started or is about to start ASAP, when the call is
	 *         executed with success.
	 * 
	 *         The command may reply with an error in certain cases, as documented
	 *         above.
	 */
	String bgrewriteaof();

	/**
	 * https://redis.io/commands/bgsave
	 * 
	 * @return Simple string reply: Background saving started if BGSAVE started
	 *         correctly or Background saving scheduled when used with the SCHEDULE
	 *         subcommand
	 */
	String bgsave();

	/**
	 * https://redis.io/commands/config-get
	 * 
	 * @param parameter
	 * @return The return type of the command is a Array reply.
	 */
	List<V> configGet(K parameter);

	/**
	 * https://redis.io/commands/config-resetstat
	 * 
	 * @return Simple string reply: always OK.
	 */
	String configResetstat();

	/**
	 * https://redis.io/commands/config-rewrite
	 * 
	 * @return Simple string reply: OK when the configuration was rewritten
	 *         properly. Otherwise an error is returned.
	 */
	String configRewrite();

	/**
	 * https://redis.io/commands/config-set
	 * 
	 * @param parameter
	 * @param value
	 * @return Simple string reply: OK when the configuration was set properly.
	 *         Otherwise an error is returned.
	 */
	String configSet(K parameter, V value);

	/**
	 * https://redis.io/commands/dbsize
	 * 
	 * @return Integer reply
	 */
	Long dbsize();

	/**
	 * @see #failover(FailoverParams)
	 */
	default String failover() {
		return failover(null);
	}

	/**
	 * https://redis.io/commands/failover
	 * 
	 * @return
	 */
	String failoverAbort();

	/**
	 * https://redis.io/commands/failover
	 * 
	 * @param params
	 * @return Simple string reply: OK if the command was accepted and a coordinated
	 *         failover is in progress. An error if the operation cannot be
	 *         executed.
	 */
	String failover(@Nullable FailoverParams params);

	/**
	 * @see #flushall(FlushMode)
	 */
	default String flushall() {
		return flushall(null);
	}

	/**
	 * https://redis.io/commands/flushall
	 * 
	 * @return Simple string reply
	 */
	String flushall(@Nullable FlushMode flushMode);

	/**
	 * @see #flushdb(FlushMode)
	 * @return
	 */
	default String flushdb() {
		return flushdb(null);
	}

	/**
	 * https://redis.io/commands/flushdb
	 * 
	 * @param flushMode
	 * @return Simple string reply
	 */
	String flushdb(@Nullable FlushMode flushMode);

	/**
	 * @see #info(String)
	 * @return
	 */
	default String info() {
		return info(null);
	}

	/**
	 * https://redis.io/commands/info
	 * 
	 * @param section
	 * @return Bulk string reply: as a collection of text lines.
	 */
	String info(@Nullable String section);

	/**
	 * https://redis.io/commands/lastsave
	 * 
	 * @return Integer reply: an UNIX time stamp.
	 */
	Long lastsave();

	String memoryDoctor();

	/**
	 * @see #memoryUsage(Object, int)
	 * @param key
	 * @return
	 */
	default Long memoryUsage(K key) {
		return memoryUsage(key, 0);
	}

	/**
	 * https://redis.io/commands/memory-usage
	 * 
	 * @param key
	 * @param samples
	 * @return Integer reply: the memory usage in bytes, or nil when the key does
	 *         not exist.
	 */
	Long memoryUsage(K key, int samples);

	/**
	 * https://redis.io/commands/module-list
	 * 
	 * @return Array reply: list of loaded modules. Each element in the list
	 *         represents a module, and is in itself a list of property names and
	 *         their values. The following properties is reported for each loaded
	 *         module:
	 * 
	 *         name: Name of the module ver: Version of the module
	 */
	List<Module> moduleList();

	/**
	 * https://redis.io/commands/module-load
	 * 
	 * @param path
	 * @return Simple string reply: OK if module was loaded.
	 */
	String moduleLoad(String path);

	/**
	 * https://redis.io/commands/module-unload
	 * 
	 * @return Simple string reply: OK if module was unloaded.
	 */
	String moduleUnload(String name);

	/**
	 * https://redis.io/commands/role
	 * 
	 * @return Array reply: where the first element is one of master, slave,
	 *         sentinel and the additional elements are role-specific as illustrated
	 *         above.
	 */
	List<Object> role();

	/**
	 * https://redis.io/commands/save
	 * 
	 * @return Simple string reply: The commands returns OK on success.
	 */
	String save();

	/**
	 * @see #shutdown(SaveMode)
	 * @return
	 */
	default void shutdown() {
		shutdown(null);
	}

	/**
	 * https://redis.io/commands/shutdown
	 * 
	 * @param saveMode
	 * @return Simple string reply: OK if ABORT was specified and shutdown was
	 *         aborted. On successful shutdown, nothing is returned since the server
	 *         quits and the connection is closed. On failure, an error is returned.
	 */
	void shutdown(@Nullable SaveMode saveMode);

	/**
	 * https://redis.io/commands/slaveof
	 * 
	 * @param host
	 * @param port
	 * @return Simple string reply
	 */
	String slaveof(String host, int port);

	/**
	 * @see #slowlogGet(Long)
	 */
	default List<Slowlog> slowlogGet() {
		return slowlogGet(null);
	}

	/**
	 * https://redis.io/commands/slowlog-get <br/>
	 * 
	 * @param count
	 * @return Array reply: a list of slow log entries.
	 */
	List<Slowlog> slowlogGet(@Nullable Long count);

	/**
	 * https://redis.io/commands/slowlog-len
	 * 
	 * @return Integer reply
	 * 
	 *         The number of entries in the slow log.
	 */
	Long slowlogLen();

	/**
	 * https://redis.io/commands/slowlog-reset
	 * 
	 * @return Simple string reply: OK
	 */
	String slowlogReset();

	/**
	 * https://redis.io/commands/swapdb
	 * 
	 * @param index1
	 * @param index2
	 * @return Simple string reply: OK if SWAPDB was executed correctly.
	 */
	String swapdb(int index1, int index2);

	/**
	 * https://redis.io/commands/time
	 * 
	 * @return Array reply, specifically:
	 * 
	 *         A multi bulk reply containing two elements:
	 * 
	 *         unix time in seconds. microseconds.
	 */
	List<String> time();
}
