package run.soeasy.framework.beans;

import run.soeasy.framework.core.convert.transform.mapping.DefaultMapper;

public class BeanMapper extends DefaultMapper {

	public BeanMapper() {
		getObjectTemplateProvider().setTemplateProvider(BeanUtils.getBeanMappingRegistry());
	}
	
}
