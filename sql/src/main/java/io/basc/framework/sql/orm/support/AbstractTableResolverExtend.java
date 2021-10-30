package io.basc.framework.sql.orm.support;

import java.util.Collection;

import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.support.AbstractObjectRelationalExtend;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableResolver;

public class AbstractTableResolverExtend extends AbstractObjectRelationalExtend implements TableResolverExtend {

	@Override
	public Collection<IndexInfo> getIndexs(Class<?> entityClass, FieldDescriptor descriptor, TableResolver chain) {
		return chain.getIndexs(entityClass, descriptor);
	}

	@Override
	public String getEngine(Class<?> entityClass, TableResolver chain) {
		return chain.getEngine(entityClass);
	}

	@Override
	public String getRowFormat(Class<?> entityClass, TableResolver chain) {
		return chain.getRowFormat(entityClass);
	}

}
