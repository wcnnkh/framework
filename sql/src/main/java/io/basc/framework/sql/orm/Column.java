package io.basc.framework.sql.orm;

import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;

public interface Column extends Property {

	Elements<IndexInfo> getIndexs();

	default boolean hasIndex() {
		return isPrimaryKey() || isUnique() || !CollectionUtils.isEmpty(getIndexs());
	}
}
