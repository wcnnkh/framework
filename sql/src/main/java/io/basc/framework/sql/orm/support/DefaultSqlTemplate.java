package io.basc.framework.sql.orm.support;

import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.orm.support.DefaultObjectKeyFormat;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.DefaultSqlOperations;
import io.basc.framework.sql.orm.SqlDialect;
import io.basc.framework.sql.orm.SqlTemplate;
import io.basc.framework.util.Assert;

public class DefaultSqlTemplate extends DefaultSqlOperations implements SqlTemplate {
	private final SqlDialect sqlDialect;
	private ObjectKeyFormat objectKeyFormat = new DefaultObjectKeyFormat();

	public DefaultSqlTemplate(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory, sqlDialect);
		this.sqlDialect = sqlDialect;
	}

	public ObjectKeyFormat getObjectKeyFormat() {
		return objectKeyFormat;
	}

	public void setObjectKeyFormat(ObjectKeyFormat objectKeyFormat) {
		Assert.requiredArgument(objectKeyFormat != null, "objectKeyFormat");
		this.objectKeyFormat = objectKeyFormat;
	}

	public SqlDialect getMapper() {
		return sqlDialect;
	}
}
