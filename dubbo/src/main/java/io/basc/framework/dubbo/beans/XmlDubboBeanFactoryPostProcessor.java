package io.basc.framework.dubbo.beans;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.beans.xml.XmlBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.dubbo.DubboConfigure;
import io.basc.framework.dubbo.DubboReferenceConfigure;
import io.basc.framework.dubbo.DubboServiceConfigure;
import io.basc.framework.dubbo.xml.XmlDubboConfigure;
import io.basc.framework.dubbo.xml.XmlDubboReferenceConfigure;
import io.basc.framework.dubbo.xml.XmlDubboServiceConfigure;

import org.apache.dubbo.config.ReferenceConfig;

@Provider
public class XmlDubboBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof XmlBeanFactory) {
			if(!beanFactory.containsDefinition(DubboConfigure.class.getName())){
				BeanDefinition definition = DefaultBeanDefinition.create(beanFactory, XmlDubboConfigure.class, () -> new XmlDubboConfigure(beanFactory.getEnvironment(), ((XmlBeanFactory) beanFactory).getConfigurationFile()));
				if(!beanFactory.containsDefinition(definition.getId())){
					beanFactory.registerDefinition(definition);
					beanFactory.registerAlias(definition.getId(), DubboConfigure.class.getName());
				}
			}
			
			if(!beanFactory.containsDefinition(DubboReferenceConfigure.class.getName())){
				BeanDefinition definition = DefaultBeanDefinition.create(beanFactory, XmlDubboReferenceConfigure.class, () -> new XmlDubboReferenceConfigure(beanFactory.getEnvironment(), ((XmlBeanFactory) beanFactory).getConfigurationFile(), beanFactory.getClassesLoaderFactory()));
				if(!beanFactory.containsDefinition(definition.getId())){
					beanFactory.registerDefinition(definition);
					beanFactory.registerAlias(definition.getId(), DubboReferenceConfigure.class.getName());
				}
			}
			
			if(!beanFactory.containsDefinition(DubboServiceConfigure.class.getName())){
				BeanDefinition definition = DefaultBeanDefinition.create(beanFactory, XmlDubboServiceConfigure.class, () -> new XmlDubboServiceConfigure(beanFactory.getEnvironment(), ((XmlBeanFactory) beanFactory).getConfigurationFile(), beanFactory.getClassesLoaderFactory(), beanFactory));
				if(!beanFactory.containsDefinition(definition.getId())){
					beanFactory.registerDefinition(definition);
					beanFactory.registerAlias(definition.getId(), DubboServiceConfigure.class.getName());
				}
			}
		}
		
		if(beanFactory.isInstance(DubboReferenceConfigure.class)){
			DubboReferenceConfigure dubboReferenceConfigure = beanFactory.getInstance(DubboReferenceConfigure.class);
			for (ReferenceConfig<?> config : dubboReferenceConfigure.getReferenceConfigList()) {
				DubboBeanDefinition xmlDubboBean = new DubboBeanDefinition(beanFactory, config.getInterfaceClass(),
						config);
				beanFactory.registerDefinition(xmlDubboBean);
			}
		}
	}

}
