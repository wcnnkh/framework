package run.soeasy.framework.http.server.cors;

import java.io.IOException;

import run.soeasy.framework.http.HttpRequestMapping;
import run.soeasy.framework.http.server.ServerHttpRequest;
import run.soeasy.framework.http.server.ServerHttpResponse;
import run.soeasy.framework.net.server.ServerException;
import run.soeasy.framework.net.server.ServerFilter;
import run.soeasy.framework.net.server.ServerRequest;
import run.soeasy.framework.net.server.ServerResponse;
import run.soeasy.framework.net.server.Service;

/**
 * 跨域注册
 * 
 * @author wcnnkh
 *
 */
public class CorsRegistry extends HttpRequestMapping<Cors> implements ServerFilter {

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Service chain)
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
