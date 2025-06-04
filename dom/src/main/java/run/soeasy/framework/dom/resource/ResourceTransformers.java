package run.soeasy.framework.dom.resource;

import java.io.IOException;

import org.w3c.dom.Node;

import run.soeasy.framework.core.io.Resource;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class ResourceTransformers extends ConfigurableServices<ResourceTransformer> implements ResourceTransformer {

	public ResourceTransformers() {
		setServiceClass(ResourceTransformer.class);
	}

	@Override
	public boolean canTransform(Node node) {
		return anyMatch((e) -> e.canTransform(node));
	}

	@Override
	public void transform(Node source, Resource resource) throws IOException {
		for (ResourceTransformer resourceTransformer : this) {
			if (resourceTransformer.canTransform(source)) {
				resourceTransformer.transform(source, resource);
				return;
			}
		}
		throw new UnsupportedOperationException(source.toString());
	}
}
