package run.soeasy.framework.beans;

import lombok.Getter;
import run.soeasy.framework.core.transform.object.ObjectMapper;

@Getter
public class BeanMapper extends ObjectMapper<BeanProperty> {
	private static final ConfigurableBeanInfoFactory BEAN_INFO_FACTORY = new ConfigurableBeanInfoFactory();
	static {
		BEAN_INFO_FACTORY.configure();
	}

	public BeanMapper() {
		getObjectTemplateRegistry().setObjectTemplateFactory(BEAN_INFO_FACTORY);
	}
}
