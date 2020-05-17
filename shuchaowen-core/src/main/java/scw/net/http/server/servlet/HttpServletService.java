package scw.net.http.server.servlet;

import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.net.http.server.DefaultHttpService;
import scw.net.http.server.HttpServiceFilter;
import scw.net.http.server.HttpServiceHandler;
import scw.net.http.server.cors.CorsFilter;
import scw.net.http.server.mvc.ControllerHandler;
import scw.net.http.server.mvc.DefaultNotfoundService;
import scw.net.http.server.mvc.MVCUtils;
import scw.net.http.server.mvc.NotFoundService;
import scw.net.http.server.mvc.action.ActionFilter;
import scw.net.http.server.mvc.action.ActionLookupManager;
import scw.net.http.server.mvc.exception.ExceptionHandler;
import scw.net.http.server.mvc.output.DefaultHttpOutput;
import scw.net.http.server.mvc.output.Output;
import scw.net.http.server.resource.DefaultResourceFactory;
import scw.net.http.server.resource.ResourceFactory;
import scw.net.http.server.resource.ServerHtttpResourceHandler;
import scw.net.http.server.rpc.ServerHttpRpcHandler;
import scw.net.rpc.RpcService;
import scw.value.property.PropertyFactory;

public class HttpServletService extends DefaultHttpService {

	public HttpServletService(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		ResourceFactory resourceFactory = new DefaultResourceFactory(MVCUtils.getSourceRoot(propertyFactory),
				MVCUtils.getResourcePaths(propertyFactory));
		ServerHtttpResourceHandler resourceHandler = new ServerHtttpResourceHandler(resourceFactory);
		handlers.add(resourceHandler);

		if (beanFactory.isInstance(RpcService.class)) {
			RpcService rpcService = beanFactory.getInstance(RpcService.class);
			ServerHttpRpcHandler rpcHandler = new ServerHttpRpcHandler(rpcService,
					MVCUtils.getRPCPath(propertyFactory));
			handlers.add(rpcHandler);
		}

		filters.add(new CorsFilter(MVCUtils.getCorsConfigFactory(beanFactory, propertyFactory)));

		filters.addAll(InstanceUtils.getConfigurationList(HttpServiceFilter.class, beanFactory, propertyFactory));
		handlers.addAll(InstanceUtils.getConfigurationList(HttpServiceHandler.class, beanFactory, propertyFactory));
	}
	
	protected void afterAppendHandlers(BeanFactory beanFactory, PropertyFactory propertyFactory){
		NotFoundService notFoundService = beanFactory.isInstance(NotFoundService.class)? beanFactory.getInstance(NotFoundService.class):new DefaultNotfoundService();
		ActionLookupManager actionLookupManager = new ActionLookupManager(beanFactory, propertyFactory);
		Output output = new DefaultHttpOutput();
		ExceptionHandler exceptionHandler = beanFactory.isInstance(ExceptionHandler.class)? null:beanFactory.getInstance(ExceptionHandler.class);
	}
}
