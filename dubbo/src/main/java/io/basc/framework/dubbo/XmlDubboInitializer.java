package io.basc.framework.dubbo;

import org.apache.dubbo.config.bootstrap.DubboBootstrap;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.xml.XmlBeanFactory;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;

/**
 * 暴露dubbo服务
 * 
 * @author shuchaowen
 *
 */
@Provider
public class XmlDubboInitializer implements ApplicationPostProcessor {

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		DubboBootstrap.getInstance().start();
		BeanFactory beanFactory = application.getBeanFactory();
		if (beanFactory instanceof XmlBeanFactory) {
			((XmlBeanFactory) beanFactory).readConfigurationFile((nodeList) -> {
				// export service
				XmlDubboExport export = new XmlDubboExport(beanFactory, nodeList);
				Thread thread = new Thread(export);
				thread.setContextClassLoader(beanFactory.getClassLoader());
				thread.setName(XmlDubboExport.class.getSimpleName());
				thread.start();
			});
		}
	}
}
