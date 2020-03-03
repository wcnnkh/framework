package scw.mvc.action.output.adapter;

import scw.beans.BeanFactory;
import scw.beans.annotation.Configuration;
import scw.core.PropertyFactory;
import scw.mvc.MVCUtils;

@Configuration(order = Integer.MIN_VALUE)
public final class ConfigurationHttpActionOutputAdapter extends
		DefaultHttpActionOutputAdapter {

	public ConfigurationHttpActionOutputAdapter(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(MVCUtils.getJsonSupport(beanFactory, propertyFactory),
				MVCUtils.getJsonp(propertyFactory));
	}
}
