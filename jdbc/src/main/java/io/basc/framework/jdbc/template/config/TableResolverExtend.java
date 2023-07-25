package io.basc.framework.jdbc.template.config;

import io.basc.framework.jdbc.template.IndexInfo;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

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
