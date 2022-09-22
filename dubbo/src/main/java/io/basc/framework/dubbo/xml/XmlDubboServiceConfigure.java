package io.basc.framework.dubbo.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.dubbo.config.ServiceConfig;
import org.w3c.dom.NodeList;

import io.basc.framework.context.Context;
import io.basc.framework.context.xml.XmlBeanUtils;
import io.basc.framework.dubbo.DubboServiceConfigure;
import io.basc.framework.io.Resource;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.stream.Processor;

public class XmlDubboServiceConfigure implements DubboServiceConfigure {
	private final List<Resource> resources;
	private final Context context;

	public XmlDubboServiceConfigure(Context context, List<Resource> resources) {
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

	@SuppressWarnings("rawtypes")
	@Override
	public List<ServiceConfig<?>> getServiceConfigList() {
		List<ServiceConfig> list = parse((nodeList) -> XmlDubboUtils.parseServiceConfigList(context, nodeList, null));
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}
		return list.stream().map((s) -> (ServiceConfig<?>) s).collect(Collectors.toList());
	}

}
