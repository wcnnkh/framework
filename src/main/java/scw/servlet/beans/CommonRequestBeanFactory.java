package scw.servlet.beans;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.servlet.beans.xml.XmlRequestBeanFactory;

public final class CommonRequestBeanFactory implements RequestBeanFactory {
	private final XmlRequestBeanFactory xmlRequestBeanFactory;
	private final AnnotationRequestBeanFactory annotationRequestBeanFactory;

	public CommonRequestBeanFactory(BeanFactory beanFactory, PropertyFactory propertyFactory, String configXml,
			String[] filterNames) throws Exception {
		this.xmlRequestBeanFactory = new XmlRequestBeanFactory(beanFactory, propertyFactory, configXml, filterNames);
		this.annotationRequestBeanFactory = new AnnotationRequestBeanFactory(beanFactory, propertyFactory,
				filterNames);
	}

	public RequestBean get(String name) {
		RequestBean requestBean = xmlRequestBeanFactory.get(name);
		if (requestBean == null) {
			requestBean = annotationRequestBeanFactory.get(name);
		}
		return requestBean;
	}
}
