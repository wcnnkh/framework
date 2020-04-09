package scw.mvc.resource;

import scw.core.instance.annotation.Configuration;
import scw.io.IOUtils;
import scw.mvc.handler.HandlerChain;
import scw.mvc.handler.HttpHandler;
import scw.mvc.http.HttpChannel;
import scw.util.value.property.PropertyFactory;

@Configuration(order=ResourceHandler.ORDER)
public final class ResourceHandler extends HttpHandler{
	public static final int ORDER = 800;
	
	private ResourceFactory resourceFactory;
	
	public ResourceHandler(PropertyFactory propertyFactory){
		this(new DefaultResourceFactory(propertyFactory));
	}
	
	public ResourceHandler(ResourceFactory resourceFactory){
		this.resourceFactory = resourceFactory;
	}
	
	@Override
	protected Object doHttpHandler(HttpChannel channel, HandlerChain chain)
			throws Throwable {
		Resource resource = resourceFactory.getResource(channel.getRequest());
		if(resource == null || resource.exists()){
			return chain.doHandler(channel);
		}
		
		IOUtils.copy(resource.getInputStream(), channel.getResponse().getBody());
		return null;
	}
}
