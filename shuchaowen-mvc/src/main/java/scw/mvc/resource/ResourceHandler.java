package scw.mvc.resource;

import scw.core.instance.annotation.Configuration;
import scw.io.IOUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.service.FilterChain;
import scw.mvc.service.HttpFilter;
import scw.util.value.property.PropertyFactory;

@Configuration(order = ResourceHandler.ORDER)
public final class ResourceHandler extends HttpFilter {
	public static final int ORDER = 800;

	private ResourceFactory resourceFactory;

	public ResourceHandler(PropertyFactory propertyFactory) {
		this(new DefaultResourceFactory(propertyFactory));
	}

	public ResourceHandler(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	@Override
	protected Object doHttpFilter(HttpChannel channel, FilterChain chain)
			throws Throwable {
		Resource resource = resourceFactory.getResource(channel.getRequest());
		if (resource == null || resource.exists()) {
			return chain.doFilter(channel);
		}

		IOUtils.copy(resource.getInputStream(), channel.getResponse().getBody());
		return null;
	}
}
