package io.basc.framework.sql.orm;

import io.basc.framework.orm.DefaultEntityMetadata;
import io.basc.framework.orm.ObjectRelationalResolver;

public class DefaultTableMetadata extends DefaultEntityMetadata implements TableMetadata {
	private final TableResolver tableResolver;

	public DefaultTableMetadata(ObjectRelationalResolver objectRelationalResolver, Class<?> entityClass,
			TableResolver tableResolver) {
		super(objectRelationalResolver, entityClass);
		this.tableResolver = tableResolver;
	}

	@Override
	public String getEngine() {
		return tableResolver.getEngine(getEntityClass());
	}

	@Override
	public String getRowFormat() {
		return tableResolver.getRowFormat(getEntityClass());
	}
}
