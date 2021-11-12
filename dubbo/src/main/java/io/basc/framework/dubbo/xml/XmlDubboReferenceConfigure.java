package io.basc.framework.dubbo.xml;

import io.basc.framework.beans.xml.XmlBeanUtils;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.dubbo.DubboReferenceConfigure;
import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;
import io.basc.framework.util.stream.Processor;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.dubbo.config.ReferenceConfig;
import org.w3c.dom.NodeList;

public class XmlDubboReferenceConfigure implements DubboReferenceConfigure {
	private final Resource resource;
	private final Environment environment;
	private final ClassesLoaderFactory classesLoaderFactory;

	public XmlDubboReferenceConfigure(Environment environment,
			Resource resource, ClassesLoaderFactory classesLoaderFactory) {
		this.environment = environment;
		this.resource = resource;
		this.classesLoaderFactory = classesLoaderFactory;
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

	public <T> T read(Processor<NodeList, T, Throwable> processor) {
		return XmlBeanUtils.readResourceBeans(environment, resource, processor);
	}

	@Override
	public List<ReferenceConfig<?>> getReferenceConfigList() {
		return read(
				(nodeList) -> XmlDubboUtils.parseReferenceConfigList(
						environment, nodeList, null, classesLoaderFactory))
				.stream().map((r) -> (ReferenceConfig<?>)r).collect(Collectors.toList());
	}

}
