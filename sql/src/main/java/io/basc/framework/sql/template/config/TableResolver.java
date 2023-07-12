package io.basc.framework.sql.template.config;

import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.sql.template.IndexInfo;
import io.basc.framework.util.Elements;

public interface TableResolver {
	Elements<IndexInfo> getIndexs(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getEngine(Class<?> sourceClass);

	String getRowFormat(Class<?> sourceClass);

	boolean isAutoCreate(Class<?> sourceClass);
}
