package io.basc.framework.beans;

import io.basc.framework.core.convert.transform.mapping.DefaultMapper;

public class BeanMapper extends DefaultMapper {

	public BeanMapper() {
		getObjectTemplateProvider().setTemplateProvider(BeanUtils.getBeanMappingRegistry());
	}
}
