package io.basc.framework.sql.orm;

import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;

public interface Column extends ColumnMetadata, Property {
	default boolean hasIndex() {
		return isPrimaryKey() || isUnique() || !CollectionUtils.isEmpty(getIndexs());
	}
}
