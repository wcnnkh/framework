package io.basc.framework.dubbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.dubbo.config.ReferenceConfig;

import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.xml.XmlBeanUtils;
import io.basc.framework.util.io.Resource;

public class DefaultDubboReferenceRegistry extends DubboRegistry implements DubboReferenceRegistry {
	private List<ReferenceConfig<?>> referenceConfigs = Collections.synchronizedList(new ArrayList<>());

	@Override
	public <T> ReferenceConfig<T> register(Class<? extends T> referenceClass) {
		ReferenceConfig<T> referenceConfig = new ReferenceConfig<>();
		referenceConfig.setInterface(referenceClass);
		register(referenceConfig);
		return referenceConfig;
	}

	@Override
	public void register(ReferenceConfig<?> config) {
		getDubboBootstrap().reference(config);
		addReferenceConfig(config);
	}

	public void addReferenceConfig(ReferenceConfig<?> referenceConfig) {
		referenceConfigs.add(referenceConfig);
	}

	@Override
	public Collection<ReferenceConfig<?>> getReferences() {
		return Collections.unmodifiableCollection(referenceConfigs);
	}

	@Override
	public void loadXml(Resource resource, ApplicationContext context) {
		super.loadXml(resource, context);

		List<ReferenceConfig<?>> referenceConfigs = XmlBeanUtils
				.parse(context.getResourceLoader(), resource,
						(nodeList) -> parseReferenceConfigList(context, nodeList, null,
								context.getClassScanner()))
				.stream().map((e) -> (ReferenceConfig<?>) e).collect(Collectors.toList());
		for (ReferenceConfig<?> referenceConfig : referenceConfigs) {
			register(referenceConfig);
		}
	}
}
