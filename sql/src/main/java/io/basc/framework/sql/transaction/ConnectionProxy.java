package io.basc.framework.sql.transaction;

import java.sql.Connection;

public interface ConnectionProxy extends Connection {
	/**
	 * 获取原始的connection
	 * 
	 * @return
	 */
	Connection getTargetConnection();
}
