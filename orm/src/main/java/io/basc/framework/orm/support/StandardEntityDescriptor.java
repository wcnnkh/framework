package io.basc.framework.orm.support;

import java.util.ArrayList;
import java.util.List;

import io.basc.framework.orm.EntityDescriptor;
import io.basc.framework.orm.PropertyMetadata;

public class StandardEntityDescriptor<T extends PropertyMetadata> extends StandardEntityMetadata
		implements EntityDescriptor<T> {
	private List<T> properties;

	public List<T> getProperties() {
		if (properties == null) {
			properties = new ArrayList<>();
		}
		return properties;
	}

	public void setProperties(List<T> properties) {
		this.properties = properties;
	}
}
