package io.basc.framework.jdbc;

import io.basc.framework.orm.EntityMapper;
import io.basc.framework.util.Assert;

public abstract class AbstractSqlOperations implements JdbcOperations {
	private final EntityMapper mapper;

	public AbstractSqlOperations(EntityMapper mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		this.mapper = mapper;
	}

	@Override
	public EntityMapper getMapper() {
		return mapper;
	}
}
