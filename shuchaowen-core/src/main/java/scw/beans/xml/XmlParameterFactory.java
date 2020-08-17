package scw.beans.xml;

import scw.beans.BeanFactory;
import scw.core.parameter.AbstractParameterFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterDescriptors;
import scw.value.property.PropertyFactory;

public class XmlParameterFactory extends AbstractParameterFactory {
	private final XmlBeanParameter[] xmlBeanParameters;
	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;

	public XmlParameterFactory(BeanFactory beanFactory, PropertyFactory propertyFactory,
			XmlBeanParameter[] xmlBeanParameters) {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
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
		return xmlBeanParameter.isAccept(parameterDescriptor, beanFactory, propertyFactory);
	}

	@Override
	protected Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) throws Exception {
		XmlBeanParameter xmlBeanParameter = xmlBeanParameters[index];
		return xmlBeanParameter.parseValue(parameterDescriptor, beanFactory, propertyFactory);
	}

}
