package scw.mvc.action;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.mvc.action.filter.ActionFilter;

@Configuration(order = ConfigurationActionHandler.ORDER)
public final class ConfigurationActionHandler extends ActionHandler {
	public static final int ORDER = -1000;

	public ConfigurationActionHandler(BeanFactory beanFactory) {
		super(beanFactory.getInstance(ActionFactory.class), InstanceUtils
				.getConfigurationList(ActionFilter.class, beanFactory));
	}
}
