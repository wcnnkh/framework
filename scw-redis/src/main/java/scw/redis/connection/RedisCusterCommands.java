package scw.redis.connection;

import java.util.List;

public interface RedisCusterCommands {
	/**
	 * https://redis.io/commands/cluster-addslots<br/>
	 * <br/>
	 * 
	 * @param slots
	 * @return Simple string reply: OK if the command was successful. Otherwise
	 *         an error is returned.
	 */
	byte[] cluster_addslots(int... slots);

	/**
	 * https://redis.io/commands/cluster-bumpepoch<br/>
	 * <br/>
	 * Advances the cluster config epoch.
	 * 
	 * The CLUSTER BUMPEPOCH command triggers an increment to the cluster's
	 * config epoch from the connected node. The epoch will be incremented if
	 * the node's config epoch is zero, or if it is less than the cluster's
	 * greatest epoch.
	 * 
	 * @return Simple string reply: BUMPED if the epoch was incremented, or
	 *         STILL if the node already has the greatest config epoch in the
	 *         cluster.
	 */
	byte[] cluster_bumpepoch();

	/**
	 * https://redis.io/commands/cluster-count-failure-reports<br/>
	 * <br/>
	 * 
	 * @param nodeId
	 * @return Integer reply: the number of active failure reports for the node.
	 */
	Integer cluster_count_failure_reports(byte[] nodeId);

	/**
	 * https://redis.io/commands/cluster-countkeysinslot<br/>
	 * <br/>
	 * 
	 * Returns the number of keys in the specified Redis Cluster hash slot. The
	 * command only queries the local data set, so contacting a node that is not
	 * serving the specified hash slot will always result in a count of zero
	 * being returned.
	 * 
	 * @param slot
	 * @return Integer reply: The number of keys in the specified hash slot, or
	 *         an error if the hash slot is invalid.
	 */
	Integer cluster_countkeysinslot(int slot);

	/**
	 * https://redis.io/commands/cluster-delslots
	 * 
	 * @param slots
	 * @return Simple string reply: OK if the command was successful. Otherwise
	 *         an error is returned.
	 */
	byte[] cluster_delslots(int... slots);

	static enum FailoverOption {
		/**
		 * manual failover when the master is down
		 */
		FORCE,
		/**
		 * manual failover without cluster consensus
		 */
		TAKEOVER
	}

	/**
	 * https://redis.io/commands/cluster-failover
	 * 
	 * @param option
	 * @return Simple string reply: OK if the command was accepted and a manual
	 *         failover is going to be attempted. An error if the operation
	 *         cannot be executed, for example if we are talking with a node
	 *         which is already a master.
	 */
	byte[] cluster_failover(FailoverOption option);

	/**
	 * https://redis.io/commands/cluster-flushslots<br/>
	 * <br/>
	 * Deletes all slots from a node.
	 * 
	 * The CLUSTER FLUSHSLOTS deletes all information about slots from the
	 * connected node. It can only be called when the database is empty.
	 * 
	 * @return Simple string reply: OK
	 */
	byte[] cluster_flushslots();

	/**
	 * https://redis.io/commands/cluster-forget<br/>
	 * <br/>
	 * The command is used in order to remove a node, specified via its node ID,
	 * from the set of known nodes of the Redis Cluster node receiving the
	 * command. In other words the specified node is removed from the nodes
	 * table of the node receiving the command.
	 * 
	 * @param nodeId
	 * @return Simple string reply: OK if the command was executed successfully,
	 *         otherwise an error is returned.
	 */
	byte[] cluster_forget(byte[] nodeId);

	/**
	 * https://redis.io/commands/cluster-getkeysinslot<br/>
	 * <br/>
	 * The command returns an array of keys names stored in the contacted node
	 * and hashing to the specified hash slot. The maximum number of keys to
	 * return is specified via the count argument, so that it is possible for
	 * the user of this API to batch-processing keys.
	 * 
	 * The main usage of this command is during rehashing of cluster slots from
	 * one node to another. The way the rehashing is performed is exposed in the
	 * Redis Cluster specification, or in a more simple to digest form, as an
	 * appendix of the CLUSTER SETSLOT command documentation.
	 * 
	 * @param slot
	 * @param count
	 * @return Array reply: From 0 to count key names in a Redis array reply.
	 */
	List<byte[]> cluster_getkeysinslot(int slot, int count);

	/**
	 * https://redis.io/commands/cluster-info
	 * 
	 * @return Bulk string reply: A map between named fields and values in the
	 *         form of <field>:<value> lines separated by newlines composed by
	 *         the two bytes CRLF.
	 */
	byte[] cluster_info();

	/**
	 * https://redis.io/commands/cluster-keyslot<br/>
	 * <br/>
	 * Returns an integer identifying the hash slot the specified key hashes to.
	 * This command is mainly useful for debugging and testing, since it exposes
	 * via an API the underlying Redis implementation of the hashing algorithm.
	 * Example use cases for this command:
	 * 
	 * Client libraries may use Redis in order to test their own hashing
	 * algorithm, generating random keys and hashing them with both their local
	 * implementation and using Redis CLUSTER KEYSLOT command, then checking if
	 * the result is the same. Humans may use this command in order to check
	 * what is the hash slot, and then the associated Redis Cluster node,
	 * responsible for a given key.
	 * 
	 * @param key
	 * @return Integer reply: The hash slot number.
	 */
	Integer cluster_keyslot(byte[] key);

	/**
	 * https://redis.io/commands/cluster-meet
	 * 
	 * @param ip
	 * @param port
	 * @return Simple string reply: OK if the command was successful. If the
	 *         address or port specified are invalid an error is returned.
	 */
	byte[] cluster_meet(byte[] ip, int port);

	/**
	 * https://redis.io/commands/cluster-myid
	 * 
	 * @return Bulk string reply: The node id.
	 */
	byte[] cluster_myid();

	/**
	 * https://redis.io/commands/cluster-nodes
	 * 
	 * @return Bulk string reply: The serialized cluster configuration.
	 * 
	 *         A note about the word slave used in this man page and command
	 *         name: Starting with Redis 5, if not for backward compatibility,
	 *         the Redis project no longer uses the word slave. Unfortunately in
	 *         this command the word slave is part of the protocol, so we'll be
	 *         able to remove such occurrences only when this API will be
	 *         naturally deprecated.
	 */
	List<byte[]> cluster_nodes();

	/**
	 * https://redis.io/commands/cluster-replicas<br/>
	 * <br/>
	 * The command provides a list of replica nodes replicating from the
	 * specified master node. The list is provided in the same format used by
	 * CLUSTER NODES (please refer to its documentation for the specification of
	 * the format).
	 * 
	 * The command will fail if the specified node is not known or if it is not
	 * a master according to the node table of the node receiving the command.
	 * 
	 * Note that if a replica is added, moved, or removed from a given master
	 * node, and we ask CLUSTER REPLICAS to a node that has not yet received the
	 * configuration update, it may show stale information. However eventually
	 * (in a matter of seconds if there are no network partitions) all the nodes
	 * will agree about the set of nodes associated with a given master.
	 * 
	 * @param nodeId
	 * @return The command returns data in the same format as CLUSTER NODES.
	 */
	List<byte[]> cluster_replicas(byte[] nodeId);

	/**
	 * https://redis.io/commands/cluster-replicate<br/>
	 * 
	 * @param nodeId
	 * @return Simple string reply: OK if the command was executed successfully,
	 *         otherwise an error is returned.
	 */
	byte[] cluster_replicate(byte[] nodeId);

	static enum ResetType {
		HARD, SLFT
	}

	/**
	 * https://redis.io/commands/cluster-reset
	 * 
	 * @param type
	 * @return Simple string reply: OK if the command was successful. Otherwise
	 *         an error is returned.
	 */
	byte[] cluster_reset(ResetType type);

	/**
	 * https://redis.io/commands/cluster-saveconfig
	 * 
	 * @return Simple string reply: OK or an error if the operation fails.
	 */
	byte[] cluster_saveconfig();

	/**
	 * https://redis.io/commands/cluster-set-config-epoch
	 * 
	 * @return Simple string reply: OK if the command was executed successfully,
	 *         otherwise an error is returned.
	 */
	byte[] cluster_set_config_epoch();

	static enum SetOption {
		IMPORTING, MIGRATING, STABLE, NODE
	}

	/**
	 * https://redis.io/commands/cluster-setslot
	 * 
	 * @param slot
	 * @param option
	 * @param nodeId
	 * @return Simple string reply: All the subcommands return OK if the command
	 *         was successful. Otherwise an error is returned.
	 */
	byte[] cluster_setslot(int slot, SetOption option, byte[] nodeId);

	/**
	 * https://redis.io/commands/cluster-slaves<br/>
	 * <br/>
	 * 
	 * @param nodeId
	 * @return The command returns data in the same format as CLUSTER NODES.
	 */
	List<byte[]> cluster_slaves(byte[] nodeId);

	/**
	 * https://redis.io/commands/cluster-slots<br/>
	 * <br/>
	 * 
	 * @return Array reply: nested list of slot ranges with IP/Port mappings.
	 */
	List<byte[]> cluster_slots();

	/**
	 * https://redis.io/commands/readonly
	 * 
	 * @return Simple string reply
	 */
	byte[] readonly();

	/**
	 * https://redis.io/commands/readwrite<br/>
	 * <br/>
	 * Disables read queries for a connection to a Redis Cluster slave node.
	 * 
	 * Read queries against a Redis Cluster slave node are disabled by default,
	 * but you can use the READONLY command to change this behavior on a per-
	 * connection basis. The READWRITE command resets the readonly mode flag of
	 * a connection back to readwrite.
	 * 
	 * @return Simple string reply
	 */
	byte[] readwrite();
}
