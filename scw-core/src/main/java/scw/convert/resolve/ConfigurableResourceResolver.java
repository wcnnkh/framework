package scw.convert.resolve;

public interface ConfigurableResourceResolver extends ResourceResolver{
	void addResourceResolver(ResourceResolver resourceResolver);
}
