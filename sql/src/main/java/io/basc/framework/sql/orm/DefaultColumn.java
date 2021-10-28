package io.basc.framework.sql.orm;

import java.util.Collection;

import io.basc.framework.mapper.Field;
import io.basc.framework.orm.DefaultProperty;
import io.basc.framework.orm.ObjectRelationalResolver;

public class DefaultColumn extends DefaultProperty implements Column {
	private final TableResolver tableResolver;

	public DefaultColumn(ObjectRelationalResolver objectRelationalResolver, Class<?> entityClass, Field field,
			TableResolver tableResolver) {
		super(objectRelationalResolver, entityClass, field);
		this.tableResolver = tableResolver;
	}

	@Override
	public Collection<IndexInfo> getIndexs() {
		return tableResolver.getIndexs(getEntityClass(), getFieldDescriptor());
	}
}
