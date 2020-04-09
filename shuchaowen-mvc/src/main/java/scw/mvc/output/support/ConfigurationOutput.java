package scw.mvc.output.support;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.mvc.output.MultiOutput;
import scw.mvc.output.Output;

public class ConfigurationOutput extends MultiOutput{
	private static final long serialVersionUID = 1L;
	
	public ConfigurationOutput(BeanFactory beanFactory){
		addAll(BeanUtils.getConfigurationList(Output.class, beanFactory));
		add(new DefaultHttpOutput());
	}
}
