package scw.mvc.output.support;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.mvc.output.MultiOutput;
import scw.mvc.output.Output;
import scw.util.value.property.PropertyFactory;

public class ConfigurationOutput extends MultiOutput{
	private static final long serialVersionUID = 1L;
	
	public ConfigurationOutput(BeanFactory beanFactory, PropertyFactory propertyFactory){
		addAll(BeanUtils.getConfigurationList(Output.class, beanFactory, propertyFactory));
		add(new DefaultHttpOutput());
	}
}
