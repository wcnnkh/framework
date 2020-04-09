package scw.net.message.converter.support;

import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.net.message.converter.MessageConverter;
import scw.net.message.converter.MultiMessageConverter;
import scw.util.value.property.PropertyFactory;

public class ConfigurationMessageConverter extends MultiMessageConverter{
	private static final long serialVersionUID = 1L;
	
	public ConfigurationMessageConverter(InstanceFactory instanceFactory, PropertyFactory propertyFactory){
		add(new AllMessageConverter());
		addAll(InstanceUtils.getConfigurationList(MessageConverter.class, instanceFactory));
	}
}