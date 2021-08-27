package io.basc.framework.convert.resolve;

public interface ConfigurableResourceResolver extends ResourceResolver, Iterable<ResourceResolver>{
	void addResourceResolver(ResourceResolver resourceResolver);
}
