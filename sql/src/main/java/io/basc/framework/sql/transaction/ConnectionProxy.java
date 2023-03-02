package io.basc.framework.sql.transaction;

import java.sql.Connection;

public interface ConnectionProxy extends Connection {
	Connection getTargetConnection();
}
