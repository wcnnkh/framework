package scw.mvc.http.filter;

import scw.beans.BeanFactory;
import scw.beans.rpc.http.RpcService;
import scw.core.PropertyFactory;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

public final class RpcServletFilter extends HttpFilter {
	private final String rpcPath;
	private final RpcService rpcService;

	public RpcServletFilter(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this.rpcService = MVCUtils.getRPCService(beanFactory, propertyFactory);
		this.rpcPath = MVCUtils.getRPCPath(propertyFactory);
	}

	protected final boolean checkRPCEnable(HttpRequest request) {
		if (rpcService == null) {
			return false;
		}

		if (!"POST".equals(request.getMethod())) {
			return false;
		}

		if (!request.getRequestPath().equals(rpcPath)) {
			return false;
		}
		return true;
	}

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse, FilterChain chain) throws Throwable {
		if(checkRPCEnable(httpRequest)){
			rpcService.service(httpRequest.getInputStream(), httpResponse.getOutputStream());
			return null;
		}
		
		return chain.doFilter(channel);
	}

}
