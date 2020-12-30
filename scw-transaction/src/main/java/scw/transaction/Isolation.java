package scw.transaction;

import java.sql.Connection;

/**
 * 事务隔离级别
 * @author shuchaowen
 *
 */
public enum Isolation {

	/**
	 * 默认的，不设置隔离级别，由实现方自己决定
	 */
	DEFAULT(-1),
	
	/**
	 * 读未提交<br/>
	 * 可以读到未提交的事物
	 */
	READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),

	/**
	 * 不可重复读<br/>
	 * 只能读提交的事物
	 */
	READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),

	/**
	 * 可重复读<br/>
	 * 事务提交前后都能读【MySql默认】
	 */
	REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),

	/**
	 * 串行化<br/>
	 * serializable时会锁表,是最安全的,也是日常开发基本不会用
	 */
	SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

	private final int level;

	Isolation(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
