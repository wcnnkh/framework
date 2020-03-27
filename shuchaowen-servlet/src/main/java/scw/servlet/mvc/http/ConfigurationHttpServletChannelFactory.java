package scw.servlet.mvc.http;

import scw.beans.BeanFactory;
import scw.mvc.MVCUtils;
import scw.util.value.property.PropertyFactory;

public final class ConfigurationHttpServletChannelFactory extends
		DefaultHttpServletChannelFactory {

	public ConfigurationHttpServletChannelFactory(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(beanFactory, MVCUtils.getJsonSupport(
				beanFactory, propertyFactory),
				MVCUtils.isSupportCookieValue(propertyFactory));
	}
}
