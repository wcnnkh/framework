package scw.mvc.http.filter;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.net.ContentType;
import scw.rpc.RpcService;

public final class RpcServletFilter extends HttpFilter {
	private final String rpcPath;
	private final RpcService rpcService;

	public RpcServletFilter(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this.rpcService = MVCUtils.getRpcService(propertyFactory, beanFactory);
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

		return StringUtils.startsWith(request.getContentType(), ContentType.APPLICATION_OCTET_STREAM, true);
	}

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse, FilterChain chain)
			throws Throwable {
		if (checkRPCEnable(httpRequest)) {
			channel.getResponse().setContentType(ContentType.APPLICATION_OCTET_STREAM);
			rpcService.service(httpRequest.getInputStream(), httpResponse.getOutputStream());
			return null;
		}

		return chain.doFilter(channel);
	}

}
