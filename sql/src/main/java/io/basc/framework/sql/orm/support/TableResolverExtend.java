package io.basc.framework.sql.orm.support;

import java.util.Collection;

import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableResolver;

public interface TableResolverExtend {

	default Collection<IndexInfo> getIndexs(Class<?> entityClass, FieldDescriptor descriptor, TableResolver chain) {
		return chain.getIndexs(entityClass, descriptor);
	}

	default String getEngine(Class<?> entityClass, TableResolver chain) {
		return chain.getEngine(entityClass);
	}

	default String getRowFormat(Class<?> entityClass, TableResolver chain) {
		return chain.getRowFormat(entityClass);
	}

	default Boolean isAutoCreate(Class<?> entityClass, TableResolver chain) {
		return chain.isAutoCreate(entityClass);
	}
}
