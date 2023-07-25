package io.basc.framework.jdbc.transaction;

import java.sql.Connection;

public interface ConnectionProxy extends Connection {
	Connection getTargetConnection();
}
