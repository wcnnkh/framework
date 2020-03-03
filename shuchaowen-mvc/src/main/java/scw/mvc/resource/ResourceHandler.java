package scw.mvc.resource;

import scw.beans.annotation.Configuration;
import scw.core.PropertyFactory;
import scw.io.IOUtils;
import scw.mvc.handler.HandlerChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpHandler;

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
	protected void doHttpHandler(HttpChannel channel, HandlerChain chain)
			throws Throwable {
		Resource resource = resourceFactory.getResource(channel.getRequest());
		if(resource == null || resource.exists()){
			chain.doHandler(channel);
			return ;
		}
		
		IOUtils.copy(resource.getInputStream(), channel.getResponse().getOutputStream());
	}
}
