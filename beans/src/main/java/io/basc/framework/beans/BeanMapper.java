package io.basc.framework.beans;

import io.basc.framework.core.mapping.stereotype.StereotypeMapper;

public class BeanMapper extends StereotypeMapper<BeanFieldDescriptor, BeanMappingDescriptor> {

	public BeanMapper() {
		getMappingDescriptorRegistry().setMappingDescriptorFactory(BeanUtils.getBeanInfoRegistry());
	}
}
