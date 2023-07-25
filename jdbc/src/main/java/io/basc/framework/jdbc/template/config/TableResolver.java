package io.basc.framework.jdbc.template.config;

import io.basc.framework.jdbc.template.IndexInfo;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

public interface TableResolver {
	Elements<IndexInfo> getIndexs(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getEngine(Class<?> sourceClass);

	String getRowFormat(Class<?> sourceClass);

	boolean isAutoCreate(Class<?> sourceClass);
}
