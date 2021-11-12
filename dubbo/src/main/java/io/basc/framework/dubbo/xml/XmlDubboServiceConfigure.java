package io.basc.framework.dubbo.xml;

import io.basc.framework.beans.xml.XmlBeanUtils;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.dubbo.DubboServiceConfigure;
import io.basc.framework.env.Environment;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.util.stream.Processor;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.dubbo.config.ServiceConfig;
import org.w3c.dom.NodeList;

public class XmlDubboServiceConfigure implements DubboServiceConfigure {
	private final Resource resource;
	private final Environment environment;
	private final ClassesLoaderFactory classesLoaderFactory;
	private final NoArgsInstanceFactory refInstanceFactory;

	public XmlDubboServiceConfigure(Environment environment, Resource resource,
			ClassesLoaderFactory classesLoaderFactory,
			NoArgsInstanceFactory refInstanceFactory) {
		this.environment = environment;
		this.resource = resource;
		this.classesLoaderFactory = classesLoaderFactory;
		this.refInstanceFactory = refInstanceFactory;
	}

	public Resource getResource() {
		return resource;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public ClassesLoaderFactory getClassesLoaderFactory() {
		return classesLoaderFactory;
	}

	public NoArgsInstanceFactory getRefInstanceFactory() {
		return refInstanceFactory;
	}

	public <T> T read(Processor<NodeList, T, Throwable> processor) {
		return XmlBeanUtils.readResourceBeans(environment, resource, processor);
	}

	@Override
	public List<ServiceConfig<?>> getServiceConfigList() {
		return read(
				(nodeList) -> XmlDubboUtils.parseServiceConfigList(environment,
						nodeList, null, refInstanceFactory,
						classesLoaderFactory)).stream().map((s) -> (ServiceConfig<?>)s)
				.collect(Collectors.toList());
	}

}
