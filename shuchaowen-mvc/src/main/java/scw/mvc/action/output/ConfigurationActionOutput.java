package scw.mvc.action.output;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.mvc.action.output.adapter.ActionOutputAdapter;
import scw.util.value.property.PropertyFactory;

public final class ConfigurationActionOutput extends MultiAdapterActionOutput{
	private static final long serialVersionUID = 1L;

	public ConfigurationActionOutput(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		addAll(BeanUtils.getConfigurationList(ActionOutputAdapter.class, beanFactory, propertyFactory));
	}
}
