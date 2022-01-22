package io.basc.framework.redis;

public enum FailoverOption {
	/**
	 * manual failover when the master is down
	 */
	FORCE,
	/**
	 * manual failover without cluster consensus
	 */
	TAKEOVER
}