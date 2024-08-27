package io.basc.framework.mapper.stereotype;

import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MappingProperties implements Properties {
	private final Mapping<?> mapping;
	private final Object target;

	@Override
	public Elements<Property> getElements() {
		return mapping.getElements().map((fieldDescriptor) -> new Field(fieldDescriptor, target));
	}

}
