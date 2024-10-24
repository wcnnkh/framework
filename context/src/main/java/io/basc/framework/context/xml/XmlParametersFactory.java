package io.basc.framework.context.xml;

import io.basc.framework.env.Environment;
import io.basc.framework.factory.support.AbstractParametersFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterDescriptors;

public class XmlParametersFactory extends AbstractParametersFactory {
	private final XmlBeanParameter[] xmlBeanParameters;
	private final Environment environment;

	public XmlParametersFactory(Environment environment, XmlBeanParameter[] xmlBeanParameters) {
		this.environment = environment;
		this.xmlBeanParameters = xmlBeanParameters;
	}

	public XmlBeanParameter[] getXmlBeanParameters() {
		return xmlBeanParameters;
	}

	@Override
	public boolean isAccept(ParameterDescriptors parameterDescriptors) {
		if (xmlBeanParameters.length != parameterDescriptors.size()) {
			return false;
		}

		return super.isAccept(parameterDescriptors);
	}

	private XmlBeanParameter getXmlBeanParameter(ParameterDescriptor parameterDescriptor, int index) {
		for (XmlBeanParameter xmlBeanParameter : xmlBeanParameters) {
			if (parameterDescriptor.getName().equals(xmlBeanParameter.getName())) {
				return xmlBeanParameter;
			}
		}
		return xmlBeanParameters[index];
	}

	@Override
	protected boolean isAccept(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		XmlBeanParameter xmlBeanParameter = getXmlBeanParameter(parameterDescriptor, index);
		return xmlBeanParameter.isAccept(parameterDescriptor, environment);
	}

	@Override
	protected Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) throws Exception {
		XmlBeanParameter xmlBeanParameter = xmlBeanParameters[index];
		return xmlBeanParameter.parseValue(parameterDescriptor, environment);
	}

}
