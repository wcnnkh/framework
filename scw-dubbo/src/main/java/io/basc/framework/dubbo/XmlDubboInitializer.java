package io.basc.framework.dubbo;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.xml.XmlBeanFactory;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;

import org.w3c.dom.NodeList;

/**
 * 暴露dubbo服务
 * 
 * @author shuchaowen
 *
 */
@Provider
public class XmlDubboInitializer implements ApplicationPostProcessor {
	
	@Override
	public void postProcessApplication(ConfigurableApplication application)
			throws Throwable {
		BeanFactory beanFactory = application.getBeanFactory();
		if (beanFactory instanceof XmlBeanFactory) {
			NodeList nodeList = ((XmlBeanFactory) beanFactory).getNodeList();
			if (nodeList == null) {
				return;
			}

			//export service
			XmlDubboExport export = new XmlDubboExport(beanFactory, nodeList);
			Thread thread = new Thread(export);
			thread.setContextClassLoader(beanFactory.getClassLoader());
			thread.setName(XmlDubboExport.class.getSimpleName());
			thread.start();
		}
	}
}
