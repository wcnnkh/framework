package io.basc.framework.dubbo.beans;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.dubbo.config.ReferenceConfig;

import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dubbo.DubboConfigure;
import io.basc.framework.dubbo.DubboReferenceConfigure;
import io.basc.framework.dubbo.DubboServiceConfigure;
import io.basc.framework.dubbo.xml.XmlDubboConfigure;
import io.basc.framework.dubbo.xml.XmlDubboReferenceConfigure;
import io.basc.framework.dubbo.xml.XmlDubboServiceConfigure;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.io.Resource;

@Provider
public class XmlDubboBeanFactoryPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		List<Resource> resources = context.getConfigurationResources().stream()
				.filter((e) -> e.exists() && e.getName().endsWith(".xml")).collect(Collectors.toList());
		if (!context.containsDefinition(DubboConfigure.class.getName())) {
			BeanDefinition definition = context.registerSupplier(TypeDescriptor.valueOf(XmlDubboConfigure.class), true,
					() -> new XmlDubboConfigure(context, resources));
			context.registerAlias(definition.getId(), DubboConfigure.class.getName());
		}

		if (!context.containsDefinition(DubboReferenceConfigure.class.getName())) {
			BeanDefinition definition = context.registerSupplier(
					TypeDescriptor.valueOf(XmlDubboReferenceConfigure.class), true,
					() -> new XmlDubboReferenceConfigure(context, resources));
			context.registerAlias(definition.getId(), DubboReferenceConfigure.class.getName());
		}

		if (!context.containsDefinition(DubboServiceConfigure.class.getName())) {
			BeanDefinition definition = context.registerSupplier(TypeDescriptor.valueOf(XmlDubboServiceConfigure.class),
					true, () -> new XmlDubboServiceConfigure(context, resources));
			if (!context.containsDefinition(definition.getId())) {
				context.registerDefinition(definition);
				context.registerAlias(definition.getId(), DubboServiceConfigure.class.getName());
			}
		}

		if (context.isInstance(DubboReferenceConfigure.class))

		{
			DubboReferenceConfigure dubboReferenceConfigure = context.getInstance(DubboReferenceConfigure.class);
			for (ReferenceConfig<?> config : dubboReferenceConfigure.getReferenceConfigList()) {
				DubboBeanDefinition xmlDubboBean = new DubboBeanDefinition(context, config.getInterfaceClass(), config);
				context.registerDefinition(xmlDubboBean);
			}
		}
	}
}
