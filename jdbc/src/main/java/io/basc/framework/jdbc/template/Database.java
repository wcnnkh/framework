package io.basc.framework.jdbc.template;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.template.dialect.SqlDialect;

public class Database extends JdbcTemplate {

	public Database(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory, sqlDialect);
	}

}
