package io.basc.framework.sql.orm;

import java.util.Collection;

import io.basc.framework.orm.PropertyMetadata;

public interface ColumnMetadata extends PropertyMetadata {
	Collection<IndexInfo> getIndexs();
}
