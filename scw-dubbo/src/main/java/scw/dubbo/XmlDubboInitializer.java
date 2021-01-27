package scw.dubbo;

import org.w3c.dom.NodeList;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.xml.XmlBeanFactory;
import scw.boot.ApplicationPostProcessor;
import scw.boot.ConfigurableApplication;
import scw.context.annotation.Provider;

/**
 * 暴露dubbo服务
 * 
 * @author shuchaowen
 *
 */
@Provider(order = Integer.MIN_VALUE)
public class XmlDubboInitializer implements ApplicationPostProcessor {
	
	@Override
	public void postProcessApplication(ConfigurableApplication application)
			throws Throwable {
		ConfigurableBeanFactory beanFactory = application.getBeanFactory();
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
