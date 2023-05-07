package io.basc.framework.sql.orm;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;

public interface TableResolver {
	Elements<IndexInfo> getIndexs(TypeDescriptor source, ParameterDescriptor descriptor);

	String getEngine(TypeDescriptor source);

	String getRowFormat(TypeDescriptor source);

	boolean isAutoCreate(TypeDescriptor source);
}
