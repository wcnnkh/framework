package io.basc.framework.mapper.property;

import io.basc.framework.value.Property;

public interface Properties extends Items<Property>, Named {
	void setElement(Property property);
}
