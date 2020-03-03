package scw.mvc.action.output;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.mvc.action.output.adapter.ActionOutputAdapter;

public final class ConfigurationActionOutput extends MultiAdapterActionOutput{
	private static final long serialVersionUID = 1L;

	public ConfigurationActionOutput(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		addAll(BeanUtils.getConfigurationList(ActionOutputAdapter.class, beanFactory, propertyFactory));
	}
}
