package io.basc.framework.sql.template.support;

import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.DefaultSqlOperations;
import io.basc.framework.sql.template.SqlTemplate;
import io.basc.framework.sql.template.dialect.SqlDialect;

public class DefaultSqlTemplate extends DefaultSqlOperations implements SqlTemplate {
	private final SqlDialect sqlDialect;

	public DefaultSqlTemplate(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory, sqlDialect);
		this.sqlDialect = sqlDialect;
	}

	public SqlDialect getMapper() {
		return sqlDialect;
	}
}
