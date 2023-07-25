package io.basc.framework.jdbc.template.support;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.DefaultSqlOperations;
import io.basc.framework.jdbc.template.JdbcTemplate;
import io.basc.framework.jdbc.template.dialect.SqlDialect;

public class DefaultSqlTemplate extends DefaultSqlOperations implements JdbcTemplate {
	private final SqlDialect sqlDialect;

	public DefaultSqlTemplate(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory, sqlDialect);
		this.sqlDialect = sqlDialect;
	}

	public SqlDialect getMapper() {
		return sqlDialect;
	}
}
