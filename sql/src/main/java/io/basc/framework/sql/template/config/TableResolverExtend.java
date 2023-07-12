package io.basc.framework.sql.template.config;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.sql.template.IndexInfo;
import io.basc.framework.util.Elements;

public interface TableResolverExtend {

	default Elements<IndexInfo> getIndexs(Class<?> entityClass, ParameterDescriptor descriptor, TableResolver chain) {
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
