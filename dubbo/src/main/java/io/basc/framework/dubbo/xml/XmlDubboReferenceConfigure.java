package io.basc.framework.dubbo.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.dubbo.config.ReferenceConfig;
import org.w3c.dom.NodeList;

import io.basc.framework.context.Context;
import io.basc.framework.context.xml.XmlBeanUtils;
import io.basc.framework.dubbo.DubboReferenceConfigure;
import io.basc.framework.io.Resource;
import io.basc.framework.util.stream.Processor;

public class XmlDubboReferenceConfigure implements DubboReferenceConfigure {
	private final List<Resource> resources;
	private final Context context;

	public XmlDubboReferenceConfigure(Context context, List<Resource> resources) {
		this.context = context;
		this.resources = resources;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public Context getContext() {
		return context;
	}

	public <T, E extends Throwable> List<T> parse(Processor<NodeList, List<T>, E> processor) throws E {
		List<T> list = new ArrayList<T>();
		for (Resource resource : resources) {
			list.addAll(XmlBeanUtils.parse(context.getResourceLoader(), resource, processor));
		}
		return list;
	}

	@Override
	public List<ReferenceConfig<?>> getReferenceConfigList() {
		return parse((nodeList) -> XmlDubboUtils.parseReferenceConfigList(context, nodeList, null,
				context.getClassesLoaderFactory())).stream().map((e) -> (ReferenceConfig<?>) e)
						.collect(Collectors.toList());
	}
}
