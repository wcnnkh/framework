package io.basc.framework.mapper.access;

import io.basc.framework.mapper.property.Items;
import io.basc.framework.mapper.property.Named;
import io.basc.framework.value.Property;

public interface ObjectAccess extends Items<Property>, Named {
	/**
	 * 设置值
	 * 
	 * @param parameter
	 */
	void setElement(Property element);
}