package scw.sql.transaction;

import java.sql.Connection;

/**
 * 事务隔离级别
 * @author shuchaowen
 *
 */
public enum Isolation {

	DEFAULT(-1),

	READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),

	READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),

	REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),

	SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

	private final int level;

	Isolation(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
