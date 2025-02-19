package io.basc.framework.http.server.cors;

import java.io.IOException;

import io.basc.framework.http.HttpRequestMapping;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.net.server.Server;
import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerFilter;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;

/**
 * 跨域注册
 * 
 * @author wcnnkh
 *
 */
public class CorsRegistry extends HttpRequestMapping<Cors> implements ServerFilter {

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Server chain)
			throws IOException, ServerException {
		if (request instanceof ServerHttpRequest && response instanceof ServerHttpResponse) {
			ServerHttpRequest serverHttpRequest = (ServerHttpRequest) request;
			ServerHttpResponse serverHttpResponse = (ServerHttpResponse) response;
			Cors cors = dispatch(request);
			if (cors != null) {
				cors.write(serverHttpRequest, serverHttpResponse.getHeaders());
			}
		}
		chain.service(request, response);
	}
}
