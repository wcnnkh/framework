package scw.mvc.output.support;

import java.util.List;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.core.instance.InstanceUtils;
import scw.mvc.output.AbstractOutput;
import scw.mvc.output.MultiOutput;
import scw.mvc.output.Output;
import scw.net.message.converter.MessageConverter;
import scw.value.property.PropertyFactory;

@Bean(proxy=false)
public class ConfigurationOutput extends MultiOutput {
	private static final long serialVersionUID = 1L;

	public ConfigurationOutput(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		addAll(InstanceUtils.getConfigurationList(Output.class, beanFactory,
				propertyFactory, ConfigurationOutput.class));
		List<MessageConverter> messageConverters = InstanceUtils
				.getConfigurationList(MessageConverter.class, beanFactory,
						propertyFactory);
		DefaultHttpOutput output = new DefaultHttpOutput();
		output.setJsonp(AbstractOutput.getJsonp(propertyFactory));
		output.getMessageConverter().addAll(messageConverters);
		add(output);
	}
}
