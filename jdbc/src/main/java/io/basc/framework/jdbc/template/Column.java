package io.basc.framework.jdbc.template;

import io.basc.framework.orm.PropertyDescriptor;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.element.Elements;

public interface Column extends PropertyDescriptor {

	Elements<IndexInfo> getIndexs();

	default boolean hasIndex() {
		return isPrimaryKey() || isUnique() || !CollectionUtils.isEmpty(getIndexs());
	}
}
