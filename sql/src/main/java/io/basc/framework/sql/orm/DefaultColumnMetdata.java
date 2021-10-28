package io.basc.framework.sql.orm;

import java.util.Collection;

import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.DefaultPropertyMetadata;
import io.basc.framework.orm.ObjectRelationalResolver;

public class DefaultColumnMetdata extends DefaultPropertyMetadata implements ColumnMetadata {
	private final TableResolver tableResolver;

	public DefaultColumnMetdata(ObjectRelationalResolver objectRelationalResolver, Class<?> entityClass,
			FieldDescriptor fieldDescriptor, TableResolver tableResolver) {
		super(objectRelationalResolver, entityClass, fieldDescriptor);
		this.tableResolver = tableResolver;
	}

	public TableResolver getTableResolver() {
		return tableResolver;
	}

	@Override
	public Collection<IndexInfo> getIndexs() {
		return tableResolver.getIndexs(getEntityClass(), getFieldDescriptor());
	}
}
