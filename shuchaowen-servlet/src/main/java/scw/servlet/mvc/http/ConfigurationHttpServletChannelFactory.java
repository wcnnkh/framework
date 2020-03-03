package scw.servlet.mvc.http;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.mvc.MVCUtils;

public final class ConfigurationHttpServletChannelFactory extends
		DefaultHttpServletChannelFactory {

	public ConfigurationHttpServletChannelFactory(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(beanFactory, MVCUtils.getJsonSupport(
				beanFactory, propertyFactory),
				MVCUtils.isSupportCookieValue(propertyFactory));
	}
}
