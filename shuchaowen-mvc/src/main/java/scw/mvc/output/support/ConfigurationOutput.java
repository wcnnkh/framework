package scw.mvc.output.support;

import java.util.List;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.mvc.output.HttpOutput;
import scw.mvc.output.MultiOutput;
import scw.mvc.output.Output;
import scw.net.message.converter.MessageConverter;
import scw.util.value.property.PropertyFactory;

public class ConfigurationOutput extends MultiOutput {
	private static final long serialVersionUID = 1L;

	public ConfigurationOutput(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		addAll(InstanceUtils.getConfigurationList(Output.class, beanFactory,
				propertyFactory));
		List<MessageConverter> messageConverters = InstanceUtils
				.getConfigurationList(MessageConverter.class, beanFactory,
						propertyFactory);
		DefaultHttpOutput output = new DefaultHttpOutput();
		output.setJsonp(HttpOutput.getJsonp(propertyFactory));
		output.getMessageConverter().addAll(messageConverters);
		add(output);
	}
}