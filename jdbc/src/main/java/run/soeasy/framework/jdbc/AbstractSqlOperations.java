package run.soeasy.framework.jdbc;

import run.soeasy.framework.orm.EntityMapper;
import run.soeasy.framework.util.Assert;

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
