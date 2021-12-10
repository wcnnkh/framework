package io.basc.framework.beans.xml;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.core.parameter.AbstractParametersFactory;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;

public class XmlParametersFactory extends AbstractParametersFactory {
	private final XmlBeanParameter[] xmlBeanParameters;
	private final BeanFactory beanFactory;

	public XmlParametersFactory(BeanFactory beanFactory, XmlBeanParameter[] xmlBeanParameters) {
		this.beanFactory = beanFactory;
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
		return xmlBeanParameter.isAccept(parameterDescriptor, beanFactory);
	}

	@Override
	protected Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) throws Exception {
		XmlBeanParameter xmlBeanParameter = xmlBeanParameters[index];
		return xmlBeanParameter.parseValue(parameterDescriptor, beanFactory);
	}

}
