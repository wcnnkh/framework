package scw.mvc.action.output.adapter;

import scw.beans.BeanFactory;
import scw.beans.annotation.Configuration;
import scw.mvc.MVCUtils;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
public final class ConfigurationHttpActionOutputAdapter extends
		DefaultHttpActionOutputAdapter {

	public ConfigurationHttpActionOutputAdapter(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(MVCUtils.getJsonSupport(beanFactory, propertyFactory),
				MVCUtils.getJsonp(propertyFactory));
	}
}
